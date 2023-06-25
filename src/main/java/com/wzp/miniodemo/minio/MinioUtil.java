package com.wzp.miniodemo.minio;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.wzp.miniodemo.config.ThreadPoolExecutorConfig;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author zp.wei
 * @date 2022/5/30 11:28
 */
@Component
public class MinioUtil {


    @Autowired
    @Qualifier("minioClient")
    private MinioClient minioClient;
    @Autowired
    private ThreadPoolExecutorConfig threadPoolConfig;

    @Autowired
    @Qualifier("myMinioS3Client")
    private AmazonS3 minioS3Client;

    /**
     * 创建bucket
     */
    public void createBucket(String bucketName) throws Exception {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    /**
     * 上传文件
     */
    public String uploadFile(MultipartFile file, String bucketName) throws Exception {
        //判断文件是否为空
        if (null == file || 0 == file.getSize()) {
            return "文件不存在，请重新检查！";
        }
        //判断存储桶是否存在  不存在则创建
        createBucket(bucketName);
        //文件名
        String filename = file.getOriginalFilename();
        //开始上传
        putObject(bucketName, filename, file.getInputStream(), file.getSize(), file.getContentType());
        return getObjectURL(bucketName, filename, 3);
    }

    /**
     * 获取全部bucket
     *
     * @return
     */
    public List<Bucket> getAllBuckets() throws Exception {
        return minioClient.listBuckets();
    }

    /**
     * 根据bucketName获取桶
     *
     * @param bucketName bucket名称
     */
    public Optional<Bucket> getBucket(String bucketName) throws Exception {
        return minioClient.listBuckets().stream().filter(b -> b.name().equals(bucketName)).findFirst();
    }

    /**
     * 根据bucketName删除桶
     *
     * @param bucketName bucket名称
     */
    public void removeBucket(String bucketName) throws Exception {
        minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
    }

    /**
     * 获取⽂件外链
     *
     * @param bucketName bucket名称
     * @param objectName ⽂件名称
     * @param expires    过期时间
     * @return url
     */
    public String getObjectURL(String bucketName, String objectName, Integer expires) throws Exception {
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs
                .builder()
                .bucket(bucketName)
                .object(objectName)
                .expiry(expires, TimeUnit.HOURS)
                .method(Method.GET)
                .build());
    }

    /**
     * 获取⽂件
     *
     * @param bucketName bucket名称
     * @param objectName ⽂件名称
     * @return ⼆进制流
     */
    public InputStream getObject(String bucketName, String objectName) throws Exception {
        return minioClient.getObject(GetObjectArgs
                .builder()
                .bucket(bucketName)
                .object(objectName)
                .build());
    }

    /**
     * 上传⽂件
     *
     * @param bucketName bucket名称
     * @param objectName ⽂件名称
     * @param stream     ⽂件流
     * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#putObject
     */
    public void putObject(String bucketName, String objectName, InputStream stream) throws Exception {
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(stream, stream.available(), -1)
                .contentType(objectName.substring(objectName.lastIndexOf("."))).build());
    }

    /**
     * 上传⽂件
     *
     * @param bucketName  bucket名称
     * @param objectName  ⽂件名称
     * @param stream      ⽂件流
     * @param size        ⼤⼩
     * @param contextType 类型
     * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#putObject
     */
    public void putObject(String bucketName, String objectName, InputStream stream, long size, String contextType) throws Exception {
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(stream, size, -1)
                .contentType(contextType).build());
    }

    /**
     * 获取⽂件信息
     *
     * @param bucketName bucket名称
     * @param objectName ⽂件名称
     * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#statObject
     */
    public StatObjectResponse getObjectInfo(String bucketName, String objectName) throws Exception {
        return minioClient.statObject(StatObjectArgs
                .builder()
                .bucket(bucketName)
                .object(objectName)
                .build());
    }

    /**
     * 删除⽂件
     *
     * @param bucketName bucket名称
     * @param objectName ⽂件名称
     * @throws Exception https://docs.minio.io/cn/java-client-apireference.html#removeObject
     */
    public void removeObject(String bucketName, String objectName) throws Exception {
        minioClient.removeObject(RemoveObjectArgs
                .builder()
                .bucket(bucketName)
                .object(objectName)
                .build());
    }


    /**
     * 列出桶里所有的对象
     *
     * @param bucketName 桶名字
     * @param recursive  是否递归查找，如果是false,就模拟文件夹结构查找
     * @param prefix     对象名称的前缀
     */
    public void listObjects(String bucketName, boolean recursive, String prefix) {
        try {
            StopWatch sw = new StopWatch();
            sw.start();
            if (minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                list(bucketName, recursive, prefix);
            }
            sw.stop();
            System.out.println("消耗时长：" + sw.getTotalTimeMillis());
        } catch (Exception e) {
            System.out.println("Error occurred: " + e);
        }
    }


    /**
     * 列出桶里所有的对象
     *
     * @param bucketName 桶名字
     * @param recursive  是否递归查找，如果是false,就模拟文件夹结构查找
     * @param prefix     对象名称的前缀
     * @throws Exception
     */
    public void list(String bucketName, boolean recursive, String prefix) throws Exception {
        Iterable<Result<Item>> myObjects = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucketName).recursive(recursive).prefix(prefix).build());
        for (Result<Item> result : myObjects) {
            Item item = result.get();
            if (item.isDir()) {
                list(bucketName, recursive, item.objectName());
            } else {
                //使用多线程往数据库写入minio文件的信息
                System.out.println("文件名：" + item.objectName() + "大小：" + item.size() + "修改时间：" + item.lastModified());
                InputStream inputStream = getObject(bucketName, item.objectName());
                FileUtil.writeFromStream(inputStream, "E:/img/" + item.objectName());
            }
        }
        System.out.println("Finished all threads");
    }


    /**
     * minio桶之间数据复制
     *
     * @param sourceBucketName 源数据桶
     * @param targetBucketName 目标数据桶
     * @param recursive        是否递归查找，如果是false,就模拟文件夹结构查找
     * @param prefix           对象名称的前缀
     */
    public void copyBuckets(String sourceBucketName, String targetBucketName, boolean recursive, String prefix) {
        try {
            createBucket(targetBucketName);
            copy(sourceBucketName, targetBucketName, recursive, prefix);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void copy(String sourceBucketName, String targetBucketName, boolean recursive, String prefix) throws Exception {
        Iterable<Result<Item>> myObjects = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(sourceBucketName).recursive(recursive).prefix(prefix).build());
        for (Result<Item> result : myObjects) {
            Item item = result.get();
            if (item.isDir()) {
                copy(sourceBucketName, targetBucketName, recursive, item.objectName());
            }
            minioClient.copyObject(CopyObjectArgs.builder().bucket(targetBucketName).object(item.objectName()).source(CopySource.builder()
                    .bucket(sourceBucketName).object(item.objectName()).build()).build());
        }
    }

    /**
     * 创建目录/上传文件
     *
     * @param objectName 目录/文件名称
     */
    public void putObject(Long objectName) throws Exception {
        String bucket = "111";
        String name = objectName + "/";
        minioClient.putObject(PutObjectArgs.builder().bucket(bucket).object(name).stream(
                new ByteArrayInputStream(new byte[0]), 0, 0).build());
    }


    /**
     * s3方式获取桶内所有文件
     */
    public void listObjectsV2(String bucket) {
        List<String> res = new ArrayList<>();
        ListObjectsRequest request = new ListObjectsRequest();
        request.setBucketName(bucket);
        boolean isTruncated = true;
        while (isTruncated) {
            ObjectListing objectListing = minioS3Client.listObjects(request);
            List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
            if (objectSummaries != null && objectSummaries.size() != 0) {
                List<String> list = objectSummaries.stream().map(x -> x.getKey()).collect(Collectors.toList());
                res.addAll(list);
                String nextMarker = objectListing.getNextMarker();
                request.setMarker(nextMarker);
            }
            //本次查询结果后面是否还有文件待查询。
            isTruncated = objectListing.isTruncated();
        }
        System.out.println(res);
//        res.forEach(item -> download(item, bucket));

    }

    /**
     * 下载指定文件
     *
     * @return inputStream
     */
    public InputStream download(String objectName, String bucket) {
        InputStream inputStream;
        try {
            //从Minio下载该文件
            GetObjectArgs.Builder builder = GetObjectArgs.builder().bucket(bucket).offset(0L);
            GetObjectArgs getObjectArgs = builder.object(objectName).build();
            inputStream = minioClient.getObject(getObjectArgs);
            FileUtil.writeFromStream(inputStream, new File("E:/img/absence3/" + objectName));
        } catch (Exception e) {
            return null;
        }
        return inputStream;
    }


}