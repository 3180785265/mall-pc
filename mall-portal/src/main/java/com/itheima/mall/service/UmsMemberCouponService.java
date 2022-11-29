package com.itheima.mall.service;

import com.itheima.mall.domain.CartPromotionItem;
import com.itheima.mall.domain.SmsCouponHistoryDetail;

import java.util.List;

public interface UmsMemberCouponService {
    List<SmsCouponHistoryDetail> listCart(List<CartPromotionItem> cartPromotionItemList, int i);
}
