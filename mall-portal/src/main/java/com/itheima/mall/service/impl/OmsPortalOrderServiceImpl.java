package com.itheima.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.mall.domain.*;
import com.itheima.mall.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OmsPortalOrderServiceImpl implements OmsPortalOrderService {
    @Autowired
    private IUmsMemberService iUmsMemberService;
    @Autowired
    private IOmsCartItemService cartItemService;
    @Autowired
    private UmsMemberCouponService memberCouponService;
    @Autowired
    private IUmsMemberReceiveAddressService memberReceiveAddressService;

    /**
     * 根据购物车信息生成订单
     *
     * @param cartIds
     * @return
     */
    @Override
    public ConfirmOrderResult generateConfirmOrder(List<Long> cartIds) {

        Long memberId = 1l;//会员id

        //获取用户购物车列表

        List<CartPromotionItem> cartPromotionItemList = cartItemService.listPromotion(memberId, cartIds);

        //查询用户收获地址列表
        LambdaQueryWrapper<UmsMemberReceiveAddress> memberReceiveAddressLambdaQueryWrapper = new LambdaQueryWrapper<>();
        memberReceiveAddressLambdaQueryWrapper.eq(UmsMemberReceiveAddress::getMemberId, memberId);
        List<UmsMemberReceiveAddress> memberReceiveAddresses = memberReceiveAddressService.list(memberReceiveAddressLambdaQueryWrapper);


        //获取用户可用的优惠卷列表
        List<SmsCouponHistoryDetail> couponHistoryDetailList = memberCouponService.listCart(cartPromotionItemList, 1);

        //总金额，活动优惠，应付金额
        BigDecimal totalAmount = cartPromotionItemList.stream().map(item -> item.getPrice()).reduce(BigDecimal.ZERO, (BigDecimal a, BigDecimal b) -> a.add(b));

        BigDecimal promotionAmount = cartPromotionItemList.stream().map(item -> item.getReduceAmount()).reduce(BigDecimal.ZERO, (BigDecimal a, BigDecimal b) -> a.add(b));
        BigDecimal payAmount = totalAmount.subtract(promotionAmount);

        ConfirmOrderResult confirmOrderResult = new ConfirmOrderResult();
        confirmOrderResult.setCartPromotionItemList(cartPromotionItemList);
        confirmOrderResult.setMemberReceiveAddressList(memberReceiveAddresses);
        confirmOrderResult.setCouponHistoryDetailList(couponHistoryDetailList);

        ConfirmOrderResult.CalcAmount calcAmount = new ConfirmOrderResult.CalcAmount();
        calcAmount.setTotalAmount(totalAmount);
        calcAmount.setFreightAmount(BigDecimal.ZERO);
        calcAmount.setPromotionAmount(promotionAmount);
        calcAmount.setPayAmount(payAmount);

        confirmOrderResult.setCalcAmount(calcAmount);
        return confirmOrderResult;
    }

    @Override
    public Map<String, Object> generateOrder(OrderParam orderParam) {
        List<OmsOrderItem> orderItemList = new ArrayList<>();
        //获取购物车及商品计算的优惠信息
        List<CartPromotionItem> cartPromotionItemList = cartItemService.listPromotion(1l, orderParam.getCarIds());

        //生成下单信息
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            //将下单的商品，设置到订单项
            OmsOrderItem orderItem = new OmsOrderItem();
            orderItem.setProductId(cartPromotionItem.getProductId());
            orderItem.setProductPic(cartPromotionItem.getProductPic());
            orderItem.setProductName(cartPromotionItem.getProductName());
            orderItem.setProductName(cartPromotionItem.getProductBrand());
            orderItem.setProductSn(cartPromotionItem.getProductSn());
            orderItem.setProductPrice(cartPromotionItem.getPrice());
            orderItem.setProductQuantity(cartPromotionItem.getQuantity());
            orderItem.setProductSkuId(cartPromotionItem.getProductSkuId());
            orderItem.setProductSkuCode(cartPromotionItem.getProductSkuCode());
            orderItem.setProductCategoryId(cartPromotionItem.getProductCategoryId());
            orderItem.setPromotionName(cartPromotionItem.getPromotionMessage());
            orderItem.setPromotionAmount(cartPromotionItem.getReduceAmount());// 每件商品促销省了多少钱
            // TODO: 2022/11/29:   orderItem.setRealAmount(???);
            //商品赠送成长值
            orderItem.setGiftIntegration(cartPromotionItem.getIntegration());
            //购买商品赠送成长值
            orderItem.setGiftGrowth(cartPromotionItem.getGrowth());
            //商品销售属性
            orderItem.setProductAttr(cartPromotionItem.getProductAttr());
            orderItemList.add(orderItem);

        }


        //校验是否有库存
        hashStock(cartPromotionItems);

        return null;
    }


}

