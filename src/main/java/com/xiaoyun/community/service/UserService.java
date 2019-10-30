package com.xiaoyun.community.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiaoyun.community.mapper.UserMapper;
import com.xiaoyun.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;


    public void createOrUpdate(User user) {

        User selectUser = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getAccountId, user.getAccountId()));
        if (selectUser != null) {
            user.setGmtModified(System.currentTimeMillis());
            user.setId(selectUser.getId());

            userMapper.updateById(user);

        } else {
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            user.setAvatarUrl(user.getAvatarUrl());
            userMapper.insert(user);
        }
    }
}
