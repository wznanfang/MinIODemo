package com.wzp.miniodemo.controller;

import com.wzp.miniodemo.domain.BaseBucket;
import com.wzp.miniodemo.event.CustomEventPublisher;
import com.wzp.miniodemo.minio.MinioUtil;
import io.minio.messages.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zp.wei
 * @date 2022/5/30 11:33
 */
@RestController
public class FileUploadController {

    @Autowired
    private MinioUtil minioUtil;
    @Resource
    private CustomEventPublisher customEventPublisher;


    /**
     * minio数据上传
     */
    @PostMapping("/upload")
    public String fileUpload(@RequestParam("file") MultipartFile file) throws Exception {
        String minioUrl = minioUtil.uploadFile(file, "nflj");
        return minioUrl;
    }


    /**
     * minio获取所有的桶
     */
    @GetMapping("/listBuckets")
    public void list() throws Exception {
        List<Bucket> list = minioUtil.getAllBuckets();
        list.forEach(bucket -> {
            System.out.println(bucket);
        });
    }


    /**
     * minio获取桶里所有的对象
     */
    @GetMapping("/listObjects")
    public void listObjects() {
        minioUtil.listObjects("nflj", false, null);
    }


    /**
     * minio桶之间数据复制
     */
    @GetMapping("/copyBuckets")
    public void copyBuckets() {
        minioUtil.copyBuckets("nflj", "nanfang", false, null);
    }


    /**
     * 事件监听读取minio数据
     */
    @GetMapping("/publish")
    public void publish() {
        BaseBucket baseBucket = new BaseBucket();
        baseBucket.setName("nflj");
        customEventPublisher.publish(baseBucket);
        System.out.println("----------处理完毕-------------");
    }


}
