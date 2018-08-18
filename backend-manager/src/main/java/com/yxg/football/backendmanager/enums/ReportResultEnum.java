package com.yxg.football.backendmanager.enums;

public enum ReportResultEnum {
    RESULT_UNDEAL("未处理", 0),
    RESULT_SUCCESS("举报成功", 1),
    RESULT_FAIL("举报失败", 2),
    RESULT_ALL("全部", 3);

    ReportResultEnum(String msg, Integer code) {
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
