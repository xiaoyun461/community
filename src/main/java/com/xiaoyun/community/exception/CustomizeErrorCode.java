package com.xiaoyun.community.exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode {

    QUERTION_NOT_FOUND(2001, "你找的问题已经不再了，要不换换试试"),
    TARGET_PARAM_NOT_FOUND(2002, "未选中任何问题"),
    NO_LOGIN(2003, "当前操作需要登录,未登录，请登录"),
    SYS_ERROR(2004, "服务器异常"),
    TYPR_PARAM_WRONG(2005, "评论内容错误，或者不存在"),
    COMMENT_NOT_FOUND(2006, "回复的评论不存在！"),
    CONTENT_IS_EMPTY(2007, "输入内容不能为空！"),
    READ_NOTFICATION_FAIL(2008, "兄弟你这是读别人的信息！"),
    NOTIFICATION(2009, "消息不翼而飞了？！")
    ;

    private String message;
    private Integer code;

    public String getMessage() {
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }


    CustomizeErrorCode(Integer code, String message) {
        this.message = message;
        this.code = code;
    }
}
