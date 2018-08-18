package com.yxg.football.backendweb.enums;

public enum PermissionEnum {
    USER_LOCK("小黑屋", 0),
    USER_POINT("积分不够", 2);

    PermissionEnum(String msg, Integer code) {
        this.msg = msg;
        this.code = code;
    }

    private String msg;
    private Integer code;

    public String getMsg() {
        return msg;
    }

    public Integer getCode() {
        return code;
    }
}
