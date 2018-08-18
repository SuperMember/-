package com.yxg.football.backendweb.handler;

import com.yxg.football.backendweb.entity.Result;
import com.yxg.football.backendweb.entity.ResultCode;
import com.yxg.football.backendweb.exceptions.FileUploadException;
import com.yxg.football.backendweb.exceptions.PermissionException;
import com.yxg.football.backendweb.exceptions.UserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ExceptionsHandler {
    @ResponseBody
    @ExceptionHandler(value = UserException.class)
    public ResponseEntity<?> handler(UserException e) {
        return new ResponseEntity<Object>(Result.genFailResult(e.getMessage()), HttpStatus.OK);
    }

    @ResponseBody
    @ExceptionHandler(value = FileUploadException.class)
    public ResponseEntity<?> handlerException(FileUploadException e) {
        return new ResponseEntity<Object>(Result.genFailResult("上传失败"), HttpStatus.OK);
    }

    @ResponseBody
    @ExceptionHandler(value = PermissionException.class)
    public ResponseEntity<?> handlerPermissionException(PermissionException e) {
        return new ResponseEntity<Object>(Result.genFailResult(e.getMessage(), ResultCode.USER_LOCK), HttpStatus.OK);
    }

}

