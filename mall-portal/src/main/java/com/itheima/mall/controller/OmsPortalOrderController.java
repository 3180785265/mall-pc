package com.itheima.mall.controller;

import com.itheima.mall.common.R;
import com.itheima.mall.domain.ConfirmOrderResult;
import com.itheima.mall.domain.OrderParam;
import com.itheima.mall.service.OmsPortalOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OmsPortalOrderController {

    @Autowired
    private OmsPortalOrderService portalOrderService;

    //    ("根据购物车信息生成确认单")
    @RequestMapping(value = "/generateConfirmOrder", method = RequestMethod.POST)
    @ResponseBody
    public R<ConfirmOrderResult> generateConfirmOrder(@RequestBody List<Long> cartIds) {
        ConfirmOrderResult confirmOrderResult = portalOrderService.generateConfirmOrder(cartIds);
        return R.success(confirmOrderResult);
    }

    /**
     * 下订单
     */
    @PostMapping("/generateOrder")
    public R generateOrder(@RequestBody OrderParam orderParam) {
        Map<String, Object> result = portalOrderService.generateOrder(orderParam);
    }
}
