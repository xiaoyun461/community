package com.xiaoyun.community.service;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiaoyun.community.dto.CommentDTO;
import com.xiaoyun.community.enums.CommentTypeEnum;
import com.xiaoyun.community.exception.CustomizeErrorCode;
import com.xiaoyun.community.exception.CustomizeException;
import com.xiaoyun.community.mapper.CommentMapper;
import com.xiaoyun.community.mapper.QuestionMapper;
import com.xiaoyun.community.mapper.UserMapper;
import com.xiaoyun.community.model.Comment;
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

    @Transactional
    public void insert(Comment comment) {
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        if (comment.getType() == null || !CommentTypeEnum.ifExist(comment.getType())) {
            throw new CustomizeException(CustomizeErrorCode.TYPR_PARAM_WRONG);
        }
        if (comment.getType() == CommentTypeEnum.COMMENT.getType()) {
            Comment dbComment = commentMapper.selectById(comment.getParentId());
            if (dbComment == null) {
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            commentMapper.insert(comment);
        } else {
            Question question = questionMapper.selectById(comment.getParentId());
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUERTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            questionMapper.update(null, new UpdateWrapper<Question>().lambda().eq(Question::getId, question.getId()).set(Question::getCommentCount, question.getCommentCount() + 1));
        }
    }

    public List<CommentDTO> listByQuestionId(Long id) {
        List<Comment> comments = commentMapper.selectList(Wrappers.<Comment>lambdaQuery()
                .eq(Comment::getParentId, id).eq(Comment::getType, CommentTypeEnum.QUESTION.getType()).orderByDesc(Comment::getGmtCreate));
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