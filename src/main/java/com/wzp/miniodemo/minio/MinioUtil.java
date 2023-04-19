package com.wzp.miniodemo.minio;

import com.wzp.miniodemo.config.ThreadPoolExecutorConfig;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zp.wei
 * @date 2022/5/30 11:28
 */
@Component
@AllArgsConstructor
public class MinioUtil {


    @Qualifier("minioClient")
    private MinioClient minioClient;
    private ThreadPoolExecutorConfig threadPoolConfig;

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
        //新的文件名 = 存储桶文件名_时间戳_格式化时间.后缀名
        //todo 后续更改为md5值作为文件名，方便使用文件的md5值进行文件是否存在的判断，以避免重复上传文件
        String fileName = "/3/" + "_" + System.currentTimeMillis() + "_" + filename.substring(filename.lastIndexOf("."));
        //开始上传
        putObject(bucketName, fileName, file.getInputStream(), file.getSize(), file.getContentType());
        return getObjectURL(bucketName, fileName, 3);
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
        ThreadPoolExecutor executor = threadPoolConfig.getExecutor();
        Iterable<Result<Item>> myObjects = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucketName).recursive(recursive).prefix(prefix).build());
        for (Result<Item> result : myObjects) {
            Item item = result.get();
            if (item.isDir()) {
                list(bucketName, recursive, item.objectName());
            } else {
                //使用多线程往数据库写入minio文件的信息
                Runnable runnable = () -> System.out.println("文件名：" + item.objectName() + "大小：" + item.size() + "修改时间：" + item.lastModified());
                executor.execute(runnable);
            }
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
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


}