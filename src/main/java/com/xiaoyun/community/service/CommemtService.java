package com.xiaoyun.community.service;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiaoyun.community.dto.CommentDTO;
import com.xiaoyun.community.enums.*;
import com.xiaoyun.community.exception.CustomizeErrorCode;
import com.xiaoyun.community.exception.CustomizeException;
import com.xiaoyun.community.mapper.CommentMapper;
import com.xiaoyun.community.mapper.NotificationMapper;
import com.xiaoyun.community.mapper.QuestionMapper;
import com.xiaoyun.community.mapper.UserMapper;
import com.xiaoyun.community.model.Comment;
import com.xiaoyun.community.model.Notification;
import com.xiaoyun.community.model.Question;
import com.xiaoyun.community.model.User;
import com.xiaoyun.community.util.RedisUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommemtService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Transactional
    public void insert(Comment comment, User commentator) {
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        if (comment.getType() == null || !CommentTypeEnum.ifExist(comment.getType())) {
            throw new CustomizeException(CustomizeErrorCode.TYPR_PARAM_WRONG);
        }
        if (comment.getType() == CommentTypeEnum.COMMENT.getType()) {
            //回复评论
            Comment dbComment;
            if (redisUtil.hasKey(RedisTypeKey.keyName(RedisKeyEnum.SELECTONE, comment.getParentId(), RedisKeyEnum.COMMENTBYID))) {
                dbComment = (Comment) redisUtil.get(RedisTypeKey.keyName(RedisKeyEnum.SELECTONE, comment.getParentId(), RedisKeyEnum.COMMENTBYID));
            } else {
                dbComment = commentMapper.selectById(comment.getParentId());
                redisUtil.set(RedisTypeKey.keyName(RedisKeyEnum.SELECTONE, comment.getParentId(), RedisKeyEnum.COMMENTBYID), dbComment, RedisTypeKey.TIMEOUTREDIS);
            }

            if (dbComment == null) {
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            //回复问题
            Question question;
            if (redisUtil.hasKey(RedisTypeKey.keyName(RedisKeyEnum.SELECTONE, dbComment.getParentId(), RedisKeyEnum.QUESTIONBYID))) {
                question = (Question) redisUtil.get(RedisTypeKey.keyName(RedisKeyEnum.SELECTONE, dbComment.getParentId(), RedisKeyEnum.QUESTIONBYID));
            } else {
                question = questionMapper.selectById(dbComment.getParentId());
                redisUtil.set(RedisTypeKey.keyName(RedisKeyEnum.SELECTONE, dbComment.getParentId(), RedisKeyEnum.QUESTIONBYID), question, RedisTypeKey.TIMEOUTREDIS);
            }
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUERTION_NOT_FOUND);
            }
            //增加评论数
            commentMapper.insert(comment);

            delComemntCache(comment);

            redisUtil.set(RedisTypeKey.keyName(RedisKeyEnum.SELECTONE, comment.getId(), RedisKeyEnum.COMMENTBYID), comment, RedisTypeKey.TIMEOUTREDIS);


            commentMapper.update(dbComment, new UpdateWrapper<Comment>().lambda().eq(Comment::getId, dbComment.getId()).set(Comment::getCommentCount, dbComment.getCommentCount() + 1));

            delComemntCache(comment);

            redisUtil.set(RedisTypeKey.keyName(RedisKeyEnum.SELECTONE, dbComment.getId(), RedisKeyEnum.COMMENTBYID), dbComment, RedisTypeKey.TIMEOUTREDIS);

            //创建通知
            createNotify(comment, dbComment.getCommentator(), commentator.getName(), question.getTitle(), NotificationTypeEnum.REPLY_COMMENT, question.getId());
        } else {
            //回复问题
            Question question;
            if (redisUtil.hasKey(RedisTypeKey.keyName(RedisKeyEnum.SELECTONE, comment.getParentId(), RedisKeyEnum.QUESTIONBYID))) {
                question = (Question) redisUtil.get(RedisTypeKey.keyName(RedisKeyEnum.SELECTONE, comment.getParentId(), RedisKeyEnum.QUESTIONBYID));
            } else {
                question = questionMapper.selectById(comment.getParentId());
                redisUtil.set(RedisTypeKey.keyName(RedisKeyEnum.SELECTONE, comment.getParentId(), RedisKeyEnum.QUESTIONBYID), question, RedisTypeKey.TIMEOUTREDIS);
            }
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUERTION_NOT_FOUND);
            }
            commentMapper.insert(comment);

            delComemntCache(comment);

            redisUtil.set(RedisTypeKey.keyName(RedisKeyEnum.SELECTONE, comment.getId(), RedisKeyEnum.COMMENTBYID), comment, RedisTypeKey.TIMEOUTREDIS);
            questionMapper.update(null, new UpdateWrapper<Question>().lambda().eq(Question::getId, question.getId()).set(Question::getCommentCount, question.getCommentCount() + 1));

            delQuestionCache(question);

            redisUtil.set(RedisTypeKey.keyName(RedisKeyEnum.SELECTONE, question.getId(), RedisKeyEnum.QUESTIONBYID), question, RedisTypeKey.TIMEOUTREDIS);
            //创建通知
            createNotify(comment, question.getCreator(), commentator.getName(), question.getTitle(), NotificationTypeEnum.REPLY_QUESTION, question.getId());
        }

    }


    private void createNotify(Comment comment, Long receiver, String notifierName, String outerTitle, NotificationTypeEnum notificationType, Long outerId) {
        if (receiver == comment.getCommentator()) {
            return;
        }
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(notificationType.getType());
        notification.setOuterId(outerId);
        notification.setNotifier(comment.getCommentator());
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setReceiver(receiver);
        notification.setNotifierName(notifierName);
        notification.setOuterTitle(outerTitle);
        notificationMapper.insert(notification);

        delNotificationCache(notification);


        redisUtil.set(RedisTypeKey.keyName(RedisKeyEnum.SELECTONE, notification.getId(), RedisKeyEnum.NOTIFICATIONBYID), notification, RedisTypeKey.TIMEOUTREDIS);
    }


    public List<CommentDTO> listByTatgetId(Long id, CommentTypeEnum type) {
        List<Comment> comments;
        if (redisUtil.hasKey(RedisTypeKey.keyName(RedisKeyEnum.SELECTLIST, id, RedisKeyEnum.COMMENTSBYPATENTIDANDTYPE, type.getType()))) {
            comments = (List<Comment>) redisUtil.get(RedisTypeKey.keyName(RedisKeyEnum.SELECTLIST, id, RedisKeyEnum.COMMENTSBYPATENTIDANDTYPE, type.getType()));
        } else {
            comments = commentMapper.selectList(Wrappers.<Comment>lambdaQuery()
                    .eq(Comment::getParentId, id).eq(Comment::getType, type.getType()).orderByDesc(Comment::getGmtCreate));
            redisUtil.set(RedisTypeKey.keyName(RedisKeyEnum.SELECTLIST, id, RedisKeyEnum.COMMENTSBYPATENTIDANDTYPE, type.getType()), comments, RedisTypeKey.TIMEOUTREDIS);
        }
        if (comments.size() == 0) {
            return new ArrayList<>();
        }
        Set<Long> commentators = comments.stream().map(Comment::getCommentator).collect(Collectors.toSet());
        List<User> users = userMapper.selectBatchIds(commentators);

        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        List<CommentDTO> commentDTOs = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());

        return commentDTOs;

    }


    private void delNotificationCache(Notification notification) {
        redisUtil.del(RedisTypeKey.keyName(RedisKeyEnum.SELECTONE, notification.getId(), RedisKeyEnum.NOTIFICATIONBYID));
        redisUtil.del(RedisTypeKey.keyName(RedisKeyEnum.SELECTCOUNT, notification.getReceiver(), RedisKeyEnum.NOTIFICATIONCOUNTBYRECEIVER));
        redisUtil.del(RedisTypeKey.keyName(RedisKeyEnum.SELECTCOUNT, notification.getReceiver(), RedisKeyEnum.NOTIFICATIONSCOUNTBYRECEIVERANDSTATUS, NotificationStatusEnum.UNREAD.getStatus()));
    }

    public void delQuestionCache(Question question) {
        redisUtil.del(RedisTypeKey.keyName(RedisKeyEnum.SELECTONE, "null", RedisKeyEnum.QUESTIONCOUNTALL));
        redisUtil.del(RedisTypeKey.keyName(RedisKeyEnum.SELECTONE, question.getId(), RedisKeyEnum.QUESTIONBYID));
        redisUtil.del(RedisTypeKey.keyName(RedisKeyEnum.SELECTCOUNT, question.getCreator(), RedisKeyEnum.QUESTIONCOUNTBYCREATOR));
        redisUtil.del(RedisTypeKey.keyName(RedisKeyEnum.SELECTLIST, question.getCreator(), RedisKeyEnum.QUESTIONSBYCREATOR));
    }

    public void delComemntCache(Comment comment) {
        redisUtil.del(RedisTypeKey.keyName(RedisKeyEnum.SELECTONE, comment.getId(), RedisKeyEnum.COMMENTBYID));
        redisUtil.del(RedisTypeKey.keyName(RedisKeyEnum.SELECTLIST, comment.getParentId(), RedisKeyEnum.COMMENTSBYPATENTIDANDTYPE, comment.getType()));
    }

}
