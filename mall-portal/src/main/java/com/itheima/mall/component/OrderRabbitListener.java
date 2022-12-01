package com.itheima.mall.component;

import com.itheima.mall.service.OmsPortalOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订单消息的消费者
 */
@Component
@Slf4j
public class OrderRabbitListener {
    @Autowired
    private OmsPortalOrderService portalOrderService;

    @RabbitListener(queues = "mall.order.cancel.ttl")
    public void listenDirectQueue1(String orderId) {
        //查询订单是否支付
        //如果未支付取消订单
        portalOrderService.cancelOrder(Long.valueOf(orderId));

        log.info("orderId-{}", orderId);
    }
}
