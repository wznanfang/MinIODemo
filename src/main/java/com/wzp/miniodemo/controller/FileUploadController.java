package com.wzp.miniodemo.controller;

import com.wzp.miniodemo.minio.MinioUtil;
import io.minio.messages.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author zp.wei
 * @date 2022/5/30 11:33
 */
@RestController
public class FileUploadController {

    @Autowired
    private MinioUtil minioUtil;

    @PostMapping("/upload")
    public String fileUpload(@RequestParam("file") MultipartFile file) throws Exception {
        String minioUrl = minioUtil.uploadFile(file, "nflj");
        return minioUrl;
    }


    @GetMapping("/list")
    public void list() throws Exception {
        List<Bucket> list = minioUtil.getAllBuckets();
        list.forEach(bucket -> {
            System.out.println(bucket);
        });
    }


    @GetMapping("/test")
    public void test() {
//        minioUtil.listObjects("nflj",false,null);
        minioUtil.copyBuckets("nflj", "nanfang", false, null);
    }


}
