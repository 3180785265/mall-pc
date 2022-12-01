package com.itheima.mall.config;

import com.itheima.mall.domain.QueueEnum;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息队列相关配置
 */
@Configuration
public class RabbitMqConfig {


    /**
     * 订单的死信交换机
     */
    @Bean
    DirectExchange orderDelayedDirect() {
        return ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getExchange())
                .delayed() //设置delay的属性为true
                .durable(true) //持久化
                .build();
    }

    /**
     * 订单的延迟队列
     *
     * @return
     */
    @Bean
    public Queue delaydQueue() {
        return new Queue(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getName());
    }


    /**
     * 将交换机和队列绑定
     *
     * @return
     */
    @Bean
    public Binding delayedDinding() {
        return BindingBuilder.bind(delaydQueue()).to(orderDelayedDirect()).with(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getRouteKey());
    }

}
