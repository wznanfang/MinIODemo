package com.wzp.miniodemo.config;

import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zp.wei
 * @date 2023/3/12 11:20
 */
@Configuration
public class ThreadPoolExecutorConfig {

    public ThreadPoolExecutor getExecutor() {
        return new ThreadPoolExecutor(6, 12, 30, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10), new ThreadPoolExecutor.CallerRunsPolicy());
    }

}
