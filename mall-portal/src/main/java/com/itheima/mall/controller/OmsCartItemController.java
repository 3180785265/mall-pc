package com.itheima.mall.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.mall.common.R;
import com.itheima.mall.domain.OmsCartItem;
import com.itheima.mall.domain.PmsSkuStock;
import com.itheima.mall.service.IPmsSkuStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 购物车表 前端控制器
 * </p>
 *
 * @author 小刘
 * @since 2022-11-25
 */
@RestController
@RequestMapping("/cartItem")
public class OmsCartItemController {

    @Autowired
    private IPmsSkuStockService iPmsSkuStockService;

    /**
     * 添加商品到购物车
     *
     * @return
     */
    @RequestMapping("/add")
    public R add(@RequestBody OmsCartItem cartItem) {

        //获取商品sku
        LambdaQueryWrapper<PmsSkuStock> pmsSkuStockLambdaQueryWrapper = new LambdaQueryWrapper<>();
        pmsSkuStockLambdaQueryWrapper.eq(PmsSkuStock::getProductId, cartItem.getProductId());
        pmsSkuStockLambdaQueryWrapper.eq(PmsSkuStock::getSkuCode, cartItem.getProductSkuCode());
        PmsSkuStock skuStock = iPmsSkuStockService.getOne(pmsSkuStockLambdaQueryWrapper);

        //获取登录会员
        ums_member

    }
}

