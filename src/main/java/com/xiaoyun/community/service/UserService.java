package com.xiaoyun.community.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiaoyun.community.enums.RedisKeyEnum;
import com.xiaoyun.community.enums.RedisTypeKey;
import com.xiaoyun.community.mapper.UserMapper;
import com.xiaoyun.community.model.User;
import com.xiaoyun.community.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisUtil redisUtil;


    public void createOrUpdate(User user) {
        User selectUser;
        if (redisUtil.hasKey(RedisTypeKey.keyName(RedisKeyEnum.SELECTONE, user.getAccountId(), RedisKeyEnum.USERBYACCOUNTID))) {
            selectUser = (User) redisUtil.get(RedisTypeKey.keyName(RedisKeyEnum.SELECTONE, user.getAccountId(), RedisKeyEnum.USERBYACCOUNTID));
        } else {
            selectUser = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getAccountId, user.getAccountId()));
            redisUtil.set(RedisTypeKey.keyName(RedisKeyEnum.SELECTONE, user.getAccountId(), RedisKeyEnum.USERBYACCOUNTID), selectUser, RedisTypeKey.TIMEOUTREDIS);
        }
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

        delUserCache(user);

        redisUtil.set(RedisTypeKey.keyName(RedisKeyEnum.SELECTONE, user.getAccountId(), RedisKeyEnum.USERBYACCOUNTID), selectUser, RedisTypeKey.TIMEOUTREDIS);
        redisUtil.set(RedisTypeKey.keyName(RedisKeyEnum.SELECTONE, user.getToken(), RedisKeyEnum.USERBYTOKEN), selectUser, RedisTypeKey.TIMEOUTREDIS);
        redisUtil.set(RedisTypeKey.keyName(RedisKeyEnum.SELECTONE, user.getId(), RedisKeyEnum.USERBYID), selectUser, RedisTypeKey.TIMEOUTREDIS);

    }

    public void delUserCache(User user) {
        redisUtil.del(RedisTypeKey.keyName(RedisKeyEnum.SELECTONE, user.getAccountId(), RedisKeyEnum.USERBYACCOUNTID));
        redisUtil.del(RedisTypeKey.keyName(RedisKeyEnum.SELECTONE, user.getToken(), RedisKeyEnum.USERBYTOKEN));
        redisUtil.del(RedisTypeKey.keyName(RedisKeyEnum.SELECTONE, user.getId(), RedisKeyEnum.USERBYID));
    }
}
