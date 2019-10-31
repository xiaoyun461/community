package com.xiaoyun.community.config.interceptor;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiaoyun.community.enums.RedisKeyEnum;
import com.xiaoyun.community.enums.RedisTypeKey;
import com.xiaoyun.community.mapper.UserMapper;
import com.xiaoyun.community.model.User;
import com.xiaoyun.community.service.NotificationService;
import com.xiaoyun.community.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Service
public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if (request.getCookies() != null && cookies.length != 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    String token = cookie.getValue();
                    User user;
                    if (redisUtil.hasKey(RedisTypeKey.keyName(RedisKeyEnum.SELECTONE, token, RedisKeyEnum.USERBYTOKEN))) {
                        user = (User) redisUtil.get(RedisTypeKey.keyName(RedisKeyEnum.SELECTONE, token, RedisKeyEnum.USERBYTOKEN));
                    } else {
                        user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getToken, token));
                        redisUtil.set((RedisTypeKey.keyName(RedisKeyEnum.SELECTONE, token, RedisKeyEnum.USERBYTOKEN)), user, TimeUnit.DAYS.toMillis(30L));
                    }
                    if (user != null) {
                        request.getSession().setAttribute("user", user);
                        Long unreadCount = notificationService.unreadCount(user.getId());
                        request.getSession().setAttribute("unreadMessage", unreadCount);
                    }
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
