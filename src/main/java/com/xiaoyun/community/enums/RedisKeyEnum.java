package com.xiaoyun.community.enums;

public enum RedisKeyEnum {

    SELECTCOUNT("selectCount_"),
    SELECTLIST("selectList_"),
    SELECTONE("selectOne_"),

    USERBYACCOUNTID("UserByAccountId"),
    USERBYTOKEN("UserByToken"),
    USERBYID("UserById"),

    COMMENTBYID("CommentById"),
    COMMENTSBYPATENTIDANDTYPE("commentsByPatentIdAndType"),

    NOTIFICATIONCOUNTBYRECEIVER("NotificationCountByReceiver"),
    NOTIFICATIONBYID("NotificationById"),
    NOTIFICATIONSCOUNTBYRECEIVERANDSTATUS("NotificationsCountByReceiverAndStatus"),

    QUESTIONCOUNTALL("QuestionCountAll"),
    QUESTIONCOUNTBYCREATOR("QuestionCountByCreator"),
    QUESTIONSBYCREATOR("QuestionsByCreator"),
    QUESTIONBYID("QuestionById");

    private String value;

    RedisKeyEnum(String value) {
        this.value = value;
    }


    public String getValue() {
        return value;
    }


}
