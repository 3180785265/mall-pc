package com.itheima.mall.component;

import com.itheima.mall.domain.QueueEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 取消订单的消息生产者
 */
@Component
@Slf4j
public class CancelOrderSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * @param orderId    订单id
     * @param delayTimes 订单超时时间
     */
    public void sendMessage(Long orderId, final long delayTimes) {
        //创建消息
        Message message = MessageBuilder

                .withBody(orderId.toString().getBytes(StandardCharsets.UTF_8))
                .setHeader("x-delay", 100000)
                .build();


        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        //给延迟队列发送消息
        rabbitTemplate.convertAndSend(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getExchange(), QueueEnum.QUEUE_TTL_ORDER_CANCEL.getRouteKey(), message, correlationData);
        log.info("send orderId:{}", orderId);
    }
}
