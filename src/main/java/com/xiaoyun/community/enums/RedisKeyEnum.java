package com.xiaoyun.community.enums;

public enum RedisKeyEnum {

    SELECTCOUNT("selectCount_"),
    SELECTLIST("selectList_"),
    SELECTONE("selectOne_"),
    ;

    private String value;

    RedisKeyEnum(String value) {
        this.value = value;
    }


    public String getValue() {
        return value;
    }


}
