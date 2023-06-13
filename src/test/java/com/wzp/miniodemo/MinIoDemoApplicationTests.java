package com.wzp.miniodemo;

import com.wzp.miniodemo.minio.MinioUtil;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SpringBootTest
class MinIoDemoApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private MinioUtil minioUtil;


    @Test
    void test() throws Exception {
        File directory = new File("E:/shuju/1000-1");
        traverse(directory);
    }
    public void traverse(File file) throws Exception {
        if (!file.isDirectory()) {
            System.out.println("File: " + file.getName());
            String name = file.getName();
            String originalFileName = file.getName();
            String contentType = "image/jpeg";
            byte[] content = Files.readAllBytes(file.toPath());
            MultipartFile multipartFile = new MockMultipartFile(name, originalFileName, contentType, content);
            //调用上传接口
            minioUtil.uploadFile(multipartFile,"5000");
        } else {
            System.out.println("Directory: " + file.getName());
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    traverse(f);
                }
            }
        }
    }


    @Test
    public void test2(){
        minioUtil.listObjectsV2("absence3");
    }


}
