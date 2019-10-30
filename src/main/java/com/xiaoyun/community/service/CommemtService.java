package com.xiaoyun.community.service;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiaoyun.community.dto.CommentDTO;
import com.xiaoyun.community.enums.CommentTypeEnum;
import com.xiaoyun.community.enums.NotificationStatusEnum;
import com.xiaoyun.community.enums.NotificationTypeEnum;
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
            Comment dbComment = commentMapper.selectById(comment.getParentId());
            if (dbComment == null) {
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            //回复问题
            Question question = questionMapper.selectById(dbComment.getParentId());
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUERTION_NOT_FOUND);
            }
            //增加评论数
            commentMapper.insert(comment);
            commentMapper.update(dbComment, new UpdateWrapper<Comment>().lambda().eq(Comment::getId, dbComment.getId()).set(Comment::getCommentCount, dbComment.getCommentCount() + 1));

            //创建通知
            createNotify(comment, dbComment.getCommentator(), commentator.getName(), question.getTitle(), NotificationTypeEnum.REPLY_COMMENT, question.getId());
        } else {
            //回复问题
            Question question = questionMapper.selectById(comment.getParentId());
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUERTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            questionMapper.update(null, new UpdateWrapper<Question>().lambda().eq(Question::getId, question.getId()).set(Question::getCommentCount, question.getCommentCount() + 1));
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
    }

    public List<CommentDTO> listByTatgetId(Long id, CommentTypeEnum type) {
        List<Comment> comments = commentMapper.selectList(Wrappers.<Comment>lambdaQuery()
                .eq(Comment::getParentId, id).eq(Comment::getType, type.getType()).orderByDesc(Comment::getGmtCreate));
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

}
