package com.yxg.football.backendweb.enums;

public enum RoleEnum {
    ROLE_USER("用户", 4),
    ROLE_OWNER("圈主", 5);

    RoleEnum(String msg, Integer code) {
        this.code = code;
        this.msg = msg;
    }

    private String msg;
    private Integer code;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
