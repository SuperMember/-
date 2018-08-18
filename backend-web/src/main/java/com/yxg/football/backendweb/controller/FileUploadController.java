package com.yxg.football.backendweb.controller;

import com.yxg.football.backendweb.entity.Result;
import com.yxg.football.backendweb.exceptions.FileUploadException;
import com.yxg.football.backendweb.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/football/api")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(HttpServletRequest request) throws Exception {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        List<String> urls = new ArrayList<>();
        try {
            urls = fileUploadService.uploadFile(files);
        } catch (Exception e) {
            e.printStackTrace();
            throw new FileUploadException("上次失败");
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(urls), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody Map<String, Object> params) {
        try {
            fileUploadService.deleteFile((List<String>) params.get("urls"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(), HttpStatus.OK);
    }
}
