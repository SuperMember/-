package com.yxg.football.backendweb.service.impl;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.exception.FdfsUnsupportStorePathException;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.yxg.football.backendweb.service.FileUploadService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    // 封装图片完整URL地址
    private String getResAccessUrl(StorePath storePath) {
        String fileUrl = "http://" + host + "/" + storePath.getFullPath();
        return fileUrl;
    }

    @Override
    public void deleteFile(List<String> urls) throws Exception {
        for (int i = 0; i < urls.size(); i++) {
            try {
                StorePath storePath = StorePath.praseFromUrl(urls.get(i));
                storageClient.deleteFile(storePath.getGroup(), storePath.getPath());
            } catch (FdfsUnsupportStorePathException e) {
                e.printStackTrace();
            }
        }
    }
}
