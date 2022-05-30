package com.wzp.miniodemo.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zp.wei
 * @date 2022/5/30 11:23
 */
@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    /**
     * 连接地址
     */
    private String endpoint;
    /**
     * 用户名
     */
    private String accessKey;
    /**
     * 密码
     */
    private String secretKey;

}
