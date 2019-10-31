package com.xiaoyun.community.enums;

import lombok.Data;

import java.util.concurrent.TimeUnit;

import static javafx.scene.input.KeyCode.T;

@Data
public class RedisTypeKey {

    public final static Long TIMEOUTREDIS = TimeUnit.DAYS.toMillis(30L);

    private String key;
    private String method;
    private String t;


    public static String keyName(RedisKeyEnum key, Object method, RedisKeyEnum t) {
        return new StringBuffer(key.getValue()).append(method).append("_").append(t.getValue()).toString();
    }

    public static String keyName(RedisKeyEnum key, Object method, RedisKeyEnum t, Object... objects) {
        return new StringBuffer(key.getValue()).append(method).append("_").append(t.getValue()).append("_").append(objects).toString();
    }

}
