package com.wzp.miniodemo.event;

import com.wzp.miniodemo.domain.BaseBucket;
import com.wzp.miniodemo.minio.MinioUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author zp.wei
 * @date 2023/3/12 10:40
 */
@Slf4j
@Component
@AllArgsConstructor
public class CustomEventListener implements ApplicationListener<CustomEvent> {

    private MinioUtil minioUtil;


    @Async
    @Override
    public void onApplicationEvent(CustomEvent event) {
        log.info("事件监听器 - 收到消息：{}", event.getBaseBucket());
        //处理数据
        BaseBucket baseBucket = event.getBaseBucket();
        minioUtil.listObjects(baseBucket.getName(), true, null);
    }

}
