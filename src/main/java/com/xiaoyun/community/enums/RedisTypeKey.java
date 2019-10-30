package com.xiaoyun.community.enums;

import lombok.Data;

@Data
public class RedisTypeKey {
    private String key;
    private String method;
    private String t;


    public static String keyName(RedisKeyEnum key, Object method, String t) {
        return new StringBuffer(key.getValue()).append(method).append("_").append(t).toString();
    }

}
