package com.xiaoyun.community.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.injector.methods.additional.AlwaysUpdateSomeColumnById;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.additional.update.impl.UpdateChainWrapper;
import com.xiaoyun.community.dto.PaginationDTO;
import com.xiaoyun.community.dto.QuestionDTO;
import com.xiaoyun.community.exception.CustomizeErrorCode;
import com.xiaoyun.community.exception.CustomizeException;
import com.xiaoyun.community.mapper.QuestionMapper;
import com.xiaoyun.community.mapper.UserMapper;
import com.xiaoyun.community.model.Question;
import com.xiaoyun.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;


    public PaginationDTO list(Integer page, Integer size) {

        PaginationDTO paginationDTO = new PaginationDTO();

        Integer totalCount = questionMapper.selectCount(null);
        Integer totalPage;
        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }

        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }
        paginationDTO.setPagination(totalPage, page);

        Integer offset = size * (page - 1);

        List<Question> questions =
                questionMapper.selectList(Wrappers.<Question>lambdaQuery().last(new StringBuffer("limit " + offset + "," + size).toString()));


        List<QuestionDTO> questionDTOList = new ArrayList<>();


        for (Question question : questions) {

            User user = getUser(question);

            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);


        return paginationDTO;

    }

    private User getUser(Question question) {
        User users = userMapper.selectById(question.getCreator());
        if (users != null) {
            return users;
        }
        return null;
    }

    public PaginationDTO list(Long userId, Integer page, Integer size) {

        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalPage;
        Integer totalCount = questionMapper.selectCount(Wrappers.<Question>lambdaQuery().eq(Question::getCreator, userId));

        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }


        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }
        paginationDTO.setPagination(totalPage, page);

        Integer offset = size * (page - 1);

        List<Question> questions =
                questionMapper.selectList(Wrappers.<Question>lambdaQuery().eq(Question::getCreator, userId).last((new StringBuffer("limit " + offset + "," + size).toString())));
        List<QuestionDTO> questionDTOList = new ArrayList<>();


        for (Question question : questions) {
            User user = getUser(question);
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);


        return paginationDTO;

    }

    public QuestionDTO getById(Long id) {
        Question question = questionMapper.selectById(id);
        if (question == null) {
            throw new CustomizeException(CustomizeErrorCode.QUERTION_NOT_FOUND);
        }

        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        User user = getUser(question);
        questionDTO.setUser(user);
        return questionDTO;

    }

    public void createOrUpdate(Question question) {
        if (question.getId() == null) {
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.insert(question);
        } else {
            int i = questionMapper.updateById(question);
            if (i != 1) {
                throw new CustomizeException(CustomizeErrorCode.QUERTION_NOT_FOUND);
            }
        }
    }

    public void incVic(Long id) {
        questionMapper.update(null, new UpdateWrapper<Question>().lambda().eq(Question::getId, questionMapper.selectById(id).getId()).set(Question::getViewCount, questionMapper.selectById(id).getViewCount() + 1));
    }
}
