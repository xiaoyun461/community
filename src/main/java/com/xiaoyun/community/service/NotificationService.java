package com.xiaoyun.community.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiaoyun.community.dto.NotificationDTO;
import com.xiaoyun.community.dto.PaginationDTO;
import com.xiaoyun.community.dto.QuestionDTO;
import com.xiaoyun.community.enums.NotificationStatusEnum;
import com.xiaoyun.community.enums.NotificationTypeEnum;
import com.xiaoyun.community.enums.RedisKeyEnum;
import com.xiaoyun.community.enums.RedisTypeKey;
import com.xiaoyun.community.exception.CustomizeErrorCode;
import com.xiaoyun.community.exception.CustomizeException;
import com.xiaoyun.community.mapper.NotificationMapper;
import com.xiaoyun.community.mapper.UserMapper;
import com.xiaoyun.community.model.Notification;
import com.xiaoyun.community.model.Question;
import com.xiaoyun.community.model.User;
import com.xiaoyun.community.util.RedisUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private RedisUtil redisUtil;

    public PaginationDTO list(Long userId, Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalPage;
        Integer totalCount = notificationMapper.selectCount(Wrappers.<Notification>lambdaQuery().eq(Notification::getReceiver, userId));

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

        List<Notification> notifications =
                notificationMapper.selectList(Wrappers.<Notification>lambdaQuery().eq(Notification::getReceiver, userId).orderByDesc(Notification::getGmtCreate).last((new StringBuffer("limit " + offset + "," + size).toString())));

        if (notifications.size() == 0) {
            return paginationDTO;
        }

        List<NotificationDTO> notificationDTOS = new ArrayList<>();
        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification, notificationDTO);
            notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
            notificationDTOS.add(notificationDTO);
        }
        paginationDTO.setData(notificationDTOS);

        return paginationDTO;
    }

    public Long unreadCount(Long id) {
        Integer integer;
        if (redisUtil.hasKey(RedisTypeKey.keyName(RedisKeyEnum.SELECTCOUNT, id, "unreadCount"))) {
            integer = (Integer) redisUtil.get(RedisTypeKey.keyName(RedisKeyEnum.SELECTCOUNT, id, "unreadCount"));
        } else {
            integer = notificationMapper.selectCount(Wrappers.<Notification>lambdaQuery().
                    eq(Notification::getReceiver, id)
                    .eq(Notification::getStatus, NotificationStatusEnum.UNREAD.getStatus()));
            redisUtil.set(RedisTypeKey.keyName(RedisKeyEnum.SELECTCOUNT, id, "unreadCount"), integer, TimeUnit.DAYS.toMillis(30L));
        }
        return Long.valueOf(integer);
    }

    public NotificationDTO read(Long id, User user) {
        Notification notification = notificationMapper.selectById(id);
        if (notification == null) {
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION);
        }
        if (!Objects.equals(notification.getReceiver(), user.getId())) {
            throw new CustomizeException(CustomizeErrorCode.READ_NOTFICATION_FAIL);
        }
        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateById(notification);

        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification, notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
        return notificationDTO;
    }
}
