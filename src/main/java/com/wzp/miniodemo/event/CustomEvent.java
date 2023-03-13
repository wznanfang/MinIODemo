package com.wzp.miniodemo.event;

import com.wzp.miniodemo.domain.BaseBucket;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author zp.wei
 * @date 2023/3/12 10:39
 */
@Getter
public class CustomEvent extends ApplicationEvent {

    private BaseBucket baseBucket;

    public CustomEvent(Object source, BaseBucket baseBucket) {
        super(source);
        this.baseBucket = baseBucket;
    }

}
