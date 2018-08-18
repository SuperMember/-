package com.yxg.football.backendmanager.enums;

public enum UserEnum {
    USER_NORMAL("正常", 0),
    USER_IMNORMAL("小黑屋", 1),
    USER_ALL("全部", 2);

    UserEnum(String msg, Integer code) {
        this.code = code;
        this.msg = msg;
    }

    private Integer code;
    private String msg;

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
