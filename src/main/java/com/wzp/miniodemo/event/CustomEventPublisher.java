package com.wzp.miniodemo.event;

import com.wzp.miniodemo.domain.BaseBucket;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * @author zp.wei
 * @date 2023/3/12 10:42
 */
@Slf4j
@Component
@AllArgsConstructor
public class CustomEventPublisher {

    private ApplicationEventPublisher publisher;


    public void publish(BaseBucket baseBucket) {
        CustomEvent customEvent = new CustomEvent(this, baseBucket);
        publisher.publishEvent(customEvent);
        log.info("事件发布成功 - 消息：{}", baseBucket);
    }

}
