package com.yxg.football.backendmanager.service.impl;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.yxg.football.backendmanager.service.FileUploadService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileUploadServiceImpl implements FileUploadService {
    @Autowired
    private FastFileStorageClient storageClient;
    @Value("${fdfs.host}")
    private String host;

    @Override
    public List<String> uploadFile(List<MultipartFile> files) throws Exception {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            StorePath storePath = storageClient.uploadFile(files.get(i).getInputStream(), files.get(i).getSize(), FilenameUtils.getExtension(files.get(i).getOriginalFilename()), null);
            list.add(getResAccessUrl(storePath));
        }
        return list;
    }


    /*
    * 上传头像
    * */
    @Override
    public String uploadProfile(List<MultipartFile> files) {
        StorePath storePath = null;
        try {
            storePath = storageClient.uploadFile(files.get(0).getInputStream(), files.get(0).getSize(), "jpg", null);
            return getResAccessUrl(storePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 封装图片完整URL地址
    private String getResAccessUrl(StorePath storePath) {
        String fileUrl = "http://" + host + "/" + storePath.getFullPath();
        return fileUrl;
    }
}
