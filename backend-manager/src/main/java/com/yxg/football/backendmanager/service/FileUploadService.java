package com.yxg.football.backendmanager.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileUploadService {
    List<String> uploadFile(List<MultipartFile> files) throws Exception;

    String uploadProfile(List<MultipartFile> files);
}
