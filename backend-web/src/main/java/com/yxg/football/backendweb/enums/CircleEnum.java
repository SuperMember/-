package com.yxg.football.backendweb.enums;

public enum CircleEnum {
    PARAM_NULL("参数不能为空", 0);

    CircleEnum(String msg, Integer code) {
    }

    public String msg;
    public Integer code;

    public String getMsg() {
        return msg;
    }


}
