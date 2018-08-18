package com.yxg.football.backendweb.enums;

public enum UserExceptionEnum {
    USER_EXIST(0,"用户名存在")
    ;

    private Integer code;
    private String msg;

    UserExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
