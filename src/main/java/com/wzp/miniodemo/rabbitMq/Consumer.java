package com.wzp.miniodemo.rabbitMq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author zp.wei
 * @date 2023/3/16 17:30
 */
public class Consumer {

    private static final String EXCHANGE_NAME = "bucketevents";
    // 主机地址
    private static final String HOST_NAME = "127.0.0.1";
    // 模式
    private static final String BIND_KEY = "*.bucketlogs";
    //交换机
    private static final String EXCHANGE_TYPE = "fanout";

    public static void main(String[] args) throws Exception {
        // 创建连接
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST_NAME);
        factory.setPort(5672);
        Connection connection = factory.newConnection();
        // 获取一个通道
        Channel channel = connection.createChannel();
        // 声明一个类型为fanout的exchange
        channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE);
        // 绑定Queue和Exchange
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, BIND_KEY);
        channel.basicConsume(queueName, true, new MyConsumer(channel));
        System.out.println("Waiting Message...");
    }
}
