package com.wzp.miniodemo.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zp.wei
 * @date 2023/3/17 21:12
 */
@Configuration
@AllArgsConstructor
public class RabbitMqConfig {

    private final RabbitMqProperties rabbitMqProperties;


    @Bean
    public DirectExchange bucketExchange() {
        return new DirectExchange(rabbitMqProperties.getExchangeName());
    }

    @Bean
    public Queue bucketQueue() {
        return QueueBuilder.durable(rabbitMqProperties.getQueueName()).build();
    }


    @Bean
    public Binding queueABindingX(Queue bucketQueue, DirectExchange bucketExchange) {
        return BindingBuilder.bind(bucketQueue).to(bucketExchange).with(rabbitMqProperties.getRoutingKey());
    }


}
