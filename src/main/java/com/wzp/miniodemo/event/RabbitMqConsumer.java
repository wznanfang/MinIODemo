package com.wzp.miniodemo.event;

import com.wzp.miniodemo.config.RabbitMqProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author zp.wei
 * @date 2023/3/17 21:17
 */
@Slf4j
@Component
@AllArgsConstructor
public class RabbitMqConsumer {


    /**
     * 方法一
     *
     * @param message
     */
//    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "bucketqueue"), exchange = @Exchange(value = "bucketexchange"), key = "bucketelogs"))
//    public void consumerNoQueue(String message) {
//        log.info("收到A队列的消息：{}", message);
//    }


    /**
     * 方法2
     *
     * @param message
     */
    @RabbitListener(queues = "bucketqueue")
    public void receiveA(Message message) {
        log.info("收到A队列的消息：{}", new String(message.getBody()));
    }


}
