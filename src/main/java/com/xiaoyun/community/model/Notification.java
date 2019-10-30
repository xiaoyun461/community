package com.xiaoyun.community.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class Notification implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long notifier;

    private Long receiver;

    private Long outerId;

    private Integer type;

    private Long gmtCreate;

    private Integer status;

    private String NotifierName;

    private String outerTitle;

}
