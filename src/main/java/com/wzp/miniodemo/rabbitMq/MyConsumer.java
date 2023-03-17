package com.wzp.miniodemo.rabbitMq;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zp.wei
 * @date 2023/3/16 17:29
 */
@Slf4j
public class MyConsumer extends DefaultConsumer {

    public MyConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
        log.info("-----------consume message----------");
        System.out.println();
        log.info("body-----: " + new String(body));
        JSONObject jsonObject = JSONObject.parseObject(new String(body));
        System.out.println(jsonObject);
        System.out.println(jsonObject.get("EventName"));
        System.out.println(jsonObject.get("Key"));
        //对key做切割获取桶名字，文件路径，文件名
        String key = jsonObject.getString("Key");
    }

}
