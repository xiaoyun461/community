package com.xiaoyun.community.service;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiaoyun.community.dto.PaginationDTO;
import com.xiaoyun.community.dto.QuestionDTO;
import com.xiaoyun.community.enums.RedisKeyEnum;
import com.xiaoyun.community.enums.RedisTypeKey;
import com.xiaoyun.community.exception.CustomizeErrorCode;
import com.xiaoyun.community.exception.CustomizeException;
import com.xiaoyun.community.mapper.QuestionMapper;
import com.xiaoyun.community.mapper.UserMapper;
import com.xiaoyun.community.model.Question;
import com.xiaoyun.community.model.User;
import com.xiaoyun.community.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisUtil redisUtil;


    public PaginationDTO list(String search, Integer page, Integer size) {
        if (StringUtils.isNotBlank(search)) {
            String[] tags = StringUtils.split(search, ",");
            search = Arrays.stream(tags).collect(Collectors.joining("|", "'", "'"));
        }


        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalCount;
        if (search == null || "".equals(search)) {
            if (redisUtil.hasKey(getCountCache(null))) {
                totalCount = (Integer) redisUtil.get(getCountCache(null));
            } else {
                totalCount = questionMapper.selectCount(null);
                redisUtil.set(getCountCache(null), totalCount, TimeUnit.DAYS.toMillis(30L));
            }
        } else {
            if (redisUtil.hasKey(getCountCache(search))) {
                totalCount = (Integer) redisUtil.get(getCountCache(search));
            } else {
                totalCount = questionMapper.selectCount(Wrappers.<Question>lambdaQuery().apply(new StringBuffer("title regexp ").append(search).toString()));
                redisUtil.set(getCountCache(search), totalCount, TimeUnit.DAYS.toMillis(30L));
            }
        }
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
        List<Question> questions;

        if (search == null || "".equals(search)) {
            if (redisUtil.hasKey(getGetCreateCache(RedisKeyEnum.SELECTLIST, "getCreate"))) {
                questions  = (List<Question>) redisUtil.get(getGetCreateCache(RedisKeyEnum.SELECTLIST, "getCreate"));
            } else {
                questions =
                        questionMapper.selectList(Wrappers.<Question>lambdaQuery().
                                orderByDesc(Question::getGmtCreate).
                                last(new StringBuffer("limit " + offset + "," + size).toString()));
                redisUtil.set(getGetCreateCache(RedisKeyEnum.SELECTLIST, "getCreate"), questions, TimeUnit.DAYS.toMillis(30L));
            }

        } else {
            if (redisUtil.hasKey(getGetCreateCache(RedisKeyEnum.SELECTLIST, search))) {
                questions = (List<Question>) redisUtil.get(getGetCreateCache(RedisKeyEnum.SELECTLIST, search));
            } else {
                questions =
                        questionMapper.selectList(Wrappers.<Question>lambdaQuery().
                                orderByDesc(Question::getGmtCreate).
                                apply(new StringBuffer("title regexp ").append(search).toString()).
                                last(new StringBuffer("limit " + offset + "," + size).toString()));
                redisUtil.set((getGetCreateCache(RedisKeyEnum.SELECTLIST, search)), questions, TimeUnit.DAYS.toMillis(30L));
            }
        }

        List<QuestionDTO> questionDTOList = new ArrayList<>();


        for (
                Question question : questions) {

            User user = getUser(question);

            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);


        return paginationDTO;

    }

    private String getGetCreateCache(RedisKeyEnum selectlist, String getCreate) {
        return RedisTypeKey.keyName(selectlist, getCreate, "question");
    }

    private String getCountCache(String value) {
        if (value == null) {
            return getGetCreateCache(RedisKeyEnum.SELECTCOUNT, "null");
        } else {
            return getGetCreateCache(RedisKeyEnum.SELECTCOUNT, value);
        }
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
        paginationDTO.setData(questionDTOList);


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

    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO) {
        if (StringUtils.isBlank(queryDTO.getTag())) {
            return new ArrayList<>();
        }
        String[] tags = StringUtils.split(queryDTO.getTag(), ",");
        String regexpTag = Arrays.stream(tags).collect(Collectors.joining("|", "'", "'"));

        List<Question> questions = questionMapper.selectList(Wrappers.<Question>lambdaQuery().ne(Question::getId, queryDTO.getId()).apply(new StringBuffer("tag regexp ").append(regexpTag).toString()));

        List<QuestionDTO> questionDTOs = questions.stream().map(q -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q, questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());

        return questionDTOs;
    }
}
