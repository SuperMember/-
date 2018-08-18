package com.yxg.football.backendmanager.controller;

import com.yxg.football.backendmanager.entity.Result;
import com.yxg.football.backendmanager.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class FileUploadController {
    @Autowired
    FileUploadService fileUploadService;

    /*
    * 文件上传
    * */
    @PostMapping("/upload")
    public ResponseEntity<?> upload(HttpServletRequest request) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        List<String> urls = new ArrayList<>();
        try {
            urls = fileUploadService.uploadFile(files);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(urls), HttpStatus.OK);
    }

    /*
    * 头像上传
    * */
    @PostMapping("/profile")
    public ResponseEntity<?> uploadProfile(HttpServletRequest request) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("avatar");
        String url = "";
        try {
            url = fileUploadService.uploadProfile(files);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Object> avatar = new HashMap<>();
        avatar.put("avatar", url);
        Map<String, Object> result = new HashMap<>();
        result.put("files", avatar);
        return new ResponseEntity<Object>(Result.genSuccessResult(result), HttpStatus.OK);
    }

}
