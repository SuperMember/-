package com.yxg.football.backendweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import org.springframework.core.io.Resource;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
@RequestMapping("/download")
public class FileDownLoadController {
    @Autowired
    RestTemplate restTemplate;

//    @GetMapping("/file")
//    @ResponseBody
//    public ResponseEntity<?> download() {
//        String url = "http://119.29.73.127/group1/M00/00/00/CodEe1qrhJ2Ad4c1ABTS0AeIBrQ982.ttf";
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Access-Control-Allow-Origin", "*");
//        headers.set("Content-Type", "application/octet-stream");
//        headers.set("Content-Disposition", "attachment;filename=CodEe1qrhJ2Ad4c1ABTS0AeIBrQ982.ttf");
//        ResponseEntity<byte[]> response = restTemplate.exchange(
//                url,
//                HttpMethod.GET,
//                new HttpEntity<byte[]>(headers),
//                byte[].class);
//        return response;
//    }

    @GetMapping("/file/{fileName}")
    public void download(HttpServletResponse res, @PathVariable String fileName) {
        res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
        res.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".ttf");
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
            os = res.getOutputStream();
            Resource resource = new ClassPathResource(fileName + ".ttf");
            File file = resource.getFile();
            bis = new BufferedInputStream(new FileInputStream(file));
            int i = bis.read(buff);
            while (i != -1) {
                os.write(buff, 0, buff.length);
                os.flush();
                i = bis.read(buff);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("success");
    }
}

