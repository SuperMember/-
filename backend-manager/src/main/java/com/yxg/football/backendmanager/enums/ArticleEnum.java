package com.yxg.football.backendmanager.enums;

public enum ArticleEnum {
    ARTICLE_ALL("全部", 4),
    ARTICLE_PASS("审核通过", 3),
    ARTICLE_UNPASS("审核未通过", 2),
    ARTICLE_CHECKING("审核中", 1),
    ARTICLE_SAVE("保存", 0);

    ArticleEnum(String msg, Integer code) {
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
