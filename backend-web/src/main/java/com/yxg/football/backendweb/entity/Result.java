package com.yxg.football.backendweb.entity;


import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 统一API响应结果封装
 */
public class Result {
    private int code;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data;

    public Result setCode(ResultCode resultCode) {
        this.code = resultCode.code;
        return this;
    }

    public int getCode() {
        return code;
    }

    public Result setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Result setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        return data;
    }

    public Result setData(Object data) {
        this.data = data;
        return this;
    }

    public static Result genSuccessResult() {
        return new Result()
                .setCode(ResultCode.SUCCESS)
                .setMessage("success");
    }

    public static Result genSuccessResult(Object data) {
        return new Result()
                .setCode(ResultCode.SUCCESS)
                .setMessage("success")
                .setData(data);
    }

    public static Result genSuccessResult(String msg) {
        return new Result()
                .setCode(ResultCode.SUCCESS)
                .setMessage(msg);
    }

    public static Result genFailResult(String message) {
        return new Result()
                .setCode(ResultCode.FAIL)
                .setMessage(message);
    }

    public static Result genFailResult(String message, ResultCode resultCode) {
        return new Result()
                .setCode(resultCode.code)
                .setMessage(message);
    }

    public static Result genServiceFailResult(String message) {
        return new Result()
                .setCode(ResultCode.INTERNAL_SERVER_ERROR)
                .setMessage(message);
    }

    public Result genResult(ResultCode resultCode, String message, Object data) {
        return new Result()
                .setCode(resultCode.code)
                .setMessage(message)
                .setData(data);
    }


}
