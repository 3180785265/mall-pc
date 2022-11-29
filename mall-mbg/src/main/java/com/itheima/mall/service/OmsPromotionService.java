package com.itheima.mall.service;

import com.itheima.mall.domain.CartPromotionItem;
import com.itheima.mall.domain.OmsCartItem;

import java.util.List;

public interface OmsPromotionService {
    /**
     * 计算购物车中的促销活动信息
     *
     * @param cartItemList 购物车
     */
    List<CartPromotionItem> calcCartPromotion(List<OmsCartItem> cartItemList);
}
