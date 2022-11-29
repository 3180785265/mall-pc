package com.itheima.mall.service;

import com.itheima.mall.domain.ConfirmOrderResult;
import com.itheima.mall.domain.OrderParam;

import java.util.List;
import java.util.Map;

public interface OmsPortalOrderService {
    ConfirmOrderResult generateConfirmOrder(List<Long> cartIds);

    Map<String, Object> generateOrder(OrderParam orderParam);

}
