package com.yxg.football.backendmanager.enums;

public enum ReportEnum {
    REPORT_COMMENT("评论", 0),
    REPORT_ARTICLE("文章", 1),
    REPORT_URL("图片", 2),
    REPORT_ALL("全部", 3);

    ReportEnum(String msg, Integer code) {
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
