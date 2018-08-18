package com.yxg.football.backendweb.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileUploadService {
    public List<String> uploadFile(List<MultipartFile> files) throws Exception;

    public void deleteFile(List<String> urls) throws Exception;
}
