package com.wzp.miniodemo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zp.wei
 * @date 2023/3/17 21:55
 */
@Configuration
@ConfigurationProperties(prefix = "spring.rabbitmq")
@Data
public class RabbitMqProperties {


    private String host;
    private String port;
    private String username;
    private String password;
    private String exchangeName;
    private String queueName;
    private String routingKey;


}
