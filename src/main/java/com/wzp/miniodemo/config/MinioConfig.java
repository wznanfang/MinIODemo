package com.wzp.miniodemo.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import io.minio.MinioClient;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zp.wei
 * @date 2022/5/30 11:26
 */
@Configuration
@EnableConfigurationProperties(MinioProperties.class)
public class MinioConfig {

    @Autowired
    private MinioProperties minioProperties;

    /**
     * 获取MinioClient
     */
    @Bean(name = "minioClient")
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }


    @Bean("myMinioS3Client")
    public AmazonS3 buildCephClient(){
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(minioProperties.getAccessKey(), minioProperties.getSecretKey());
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setConnectionTimeout(20000);
        clientConfiguration.setProtocol(Protocol.HTTP);
        AmazonS3 amazonS3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(minioProperties.getEndpoint(),"default"))
                .withClientConfiguration(clientConfiguration)
                .build();
        return amazonS3Client;
    }


}
