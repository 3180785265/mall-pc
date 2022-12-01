package com.itheima.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.itheima.mall.common.exception.BusException;
import com.itheima.mall.component.CancelOrderSender;
import com.itheima.mall.domain.*;
import com.itheima.mall.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Autowired
    private IUmsIntegrationConsumeSettingService integrationConsumeSettingService;
    @Autowired
    private IPmsSkuStockService iPmsSkuStockService;
    @Autowired
    private IOmsOrderSettingService orderSettingService;
    @Autowired
    private IOmsOrderItemService orderItemService;
    @Autowired
    private IOmsOrderService orderService;
    @Autowired
    private ISmsCouponHistoryService couponHistoryService;
    @Autowired
    private CancelOrderSender cancelOrderSender;

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
    @Transactional
    public Map<String, Object> generateOrder(OrderParam orderParam) {
        List<OmsOrderItem> orderItemList = new ArrayList<>();
        //获取购物车及商品计算的优惠信息
        List<CartPromotionItem> cartPromotionItemList = cartItemService.listPromotion(1l, orderParam.getCarIds());
        if (CollectionUtils.isEmpty(cartPromotionItemList)) {
            throw new BusException("商品不存在");
        }

        //校验是否有库存
        if (!hasStock(cartPromotionItemList)) {
            throw new BusException("商品库存不足！无法下单");
        }

        //生成下单信息
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            //将下单的商品，设置到订单项
            OmsOrderItem orderItem = new OmsOrderItem();
            orderItem.setProductId(cartPromotionItem.getProductId());
            orderItem.setProductPic(cartPromotionItem.getProductPic());
            orderItem.setProductName(cartPromotionItem.getProductName());
            orderItem.setProductBrand(cartPromotionItem.getProductBrand());
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
            //优惠卷分摊金额
            orderItem.setCouponAmount(BigDecimal.ZERO);
            //积分优惠分摊的金额
            orderItem.setPromotionAmount(BigDecimal.ZERO);

            orderItemList.add(orderItem);


        }

        UmsMember member = iUmsMemberService.getById(1l);


        //判断用户是否使用了优惠卷
        if (orderParam.getCouponId() != null) {
            //根据购物车商品和用户id，查询用户可使用的优惠卷
            List<SmsCouponHistoryDetail> couponHistoryDetailList = memberCouponService.listCart(cartPromotionItemList, 1);

            List<SmsCouponHistoryDetail> historyDetails = couponHistoryDetailList.stream().filter(couponHistoryDetail -> couponHistoryDetail.getCouponId().equals(orderParam.getCouponId())).collect(Collectors.toList());
            //如果对应的优惠卷不存在
            if (CollectionUtils.isEmpty(historyDetails)) {
                throw new BusException("该优惠卷不可用");
            }
            //对订单商品中，可以使用优惠卷的商品，进行优惠处理
            handleCouponAmount(orderItemList, historyDetails.get(0));

        }

        BigDecimal totalAmount = BigDecimal.ZERO;//订单总金额
        for (OmsOrderItem orderItem : orderItemList) {
            totalAmount = totalAmount.add(orderItem.getProductPrice().multiply(new BigDecimal(orderItem.getProductQuantity())));
        }

        int count = 0; //商品数量
        for (OmsOrderItem orderItem : orderItemList) {
            count += orderItem.getProductQuantity();
        }


        //判断用户是否使用了积分
        if (orderParam.getUseIntegration() != null || !orderParam.getUseIntegration().equals(0)) {
            //如果用户使用了积分
            //根据用户积分，计算可以减免的金额
            UmsIntegrationConsumeSetting integrationConsumeSetting = integrationConsumeSettingService.getById(1l);
            Integer useUnit = integrationConsumeSetting.getUseUnit();//每个订单达到多少积分才可以抵扣
            Integer deductionPerAmount = integrationConsumeSetting.getDeductionPerAmount();//每一元需要抵扣多少积分
            // 将每笔订单最高抵用百分比，进行分转元处理
            BigDecimal maxPercentPerOrder = new BigDecimal(integrationConsumeSetting.getMaxPercentPerOrder()).divide(new BigDecimal("100"), 2, RoundingMode.DOWN);
            //判断积分是否可以和优惠卷一起使用
            if (integrationConsumeSetting.getCouponStatus().equals(1) || (integrationConsumeSetting.getCouponStatus().equals(0) && orderParam.getCouponId() == null)) {
                //判断用户的积分是否大于要被扣减的积分，并且使用的积分是否大于订单最少要使用的积分
                if (Integer.compare(member.getIntegration(), orderParam.getUseIntegration()) >= 0 && Integer.compare(orderParam.getUseIntegration(), useUnit) >= 0) {
                    //计算出最高可以抵扣多少金额

                    BigDecimal maxPercentPerOrderAmoumt = totalAmount.multiply(maxPercentPerOrder);//积分最高可以减免的金额
                    //计算订单最大可使用的积分
                    Integer maxPercentPerOrderIntegration = maxPercentPerOrderAmoumt.multiply(new BigDecimal(deductionPerAmount)).intValue();
                    //判断用户的积分是否大于了订单最多可使用的积分
                    if (Integer.compare(member.getIntegration(), maxPercentPerOrderIntegration) >= 0) {

                        BigDecimal promotionAmount = maxPercentPerOrderAmoumt.divide(new BigDecimal(count), 2, RoundingMode.DOWN);

                        for (OmsOrderItem orderItem : orderItemList) {
                            orderItem.setPromotionAmount(promotionAmount);
                        }

                    } else {
                        //如果用户的积分小于最大可使用的积分
                        BigDecimal totalPromotionAmount = new BigDecimal(member.getIntegration()).divide(new BigDecimal(deductionPerAmount));// 该订单使用用户积分可以优惠的金额
                        BigDecimal promotionAmount = totalPromotionAmount.divide(new BigDecimal(count), 2, RoundingMode.DOWN);
                        for (OmsOrderItem orderItem : orderItemList) {
                            orderItem.setIntegrationAmount(promotionAmount);
                        }
                    }


                }


            }
        }


        //计算每个商品经过优惠后的分解金额(原价-促销分解金额-优惠券优惠分解金额-积分优惠分解金额 )

        //计算order_item的实付金额 应付金额= 分解金额*商品数量
        handleRealAmount(orderItemList);

        //对商品进行库存锁定
        for (OmsOrderItem orderItem : orderItemList) {
            PmsSkuStock stock = iPmsSkuStockService.getOne(new LambdaQueryWrapper<PmsSkuStock>().eq(PmsSkuStock::getId, orderItem.getProductSkuId()));

            stock.setLockStock(stock.getLockStock() + orderItem.getProductQuantity());
            iPmsSkuStockService.updateById(stock);

        }

        //根据商品合计，运费，活动优惠，优惠价，积分等，计算出用户最终应付的金额
        OmsOrder order = new OmsOrder();
        order.setDiscountAmount(new BigDecimal(0));
        order.setTotalAmount(totalAmount);
        order.setFreightAmount(new BigDecimal(0));
        order.setPromotionAmount(calcPromotionAmount(orderItemList));
        order.setPromotionInfo(getOrderPromotionInfo(orderItemList));

        order.setMemberId(1l);
        order.setCreateTime(LocalDateTime.now());
        order.setMemberUsername(member.getUsername());
        //支付方式
        order.setPayType(order.getPayType());
        //订单来源
        order.setSourceType(1);
        //订单状态
        order.setStatus(0);
        //订单类型
        order.setOrderType(0);
        //收货人信息:
        UmsMemberReceiveAddress address = memberReceiveAddressService.getById(orderParam.getMemberReceiveAddressId());
        order.setReceiverName(address.getName());
        order.setReceiverPhone(address.getPhoneNumber());
        order.setReceiverProvince(address.getProvince());
        order.setReceiverCity(address.getCity());
        order.setReceiverRegion(address.getRegion());
        order.setReceiverDetailAddress(address.getDetailAddress());

        //确认收获状态  0->未确认；1->已确认
        order.setConfirmStatus(0);
        order.setDeleteStatus(0);


        //设置订单总共使用优惠卷，优惠减免金额
        if (orderParam.getCouponId() != null) {
            order.setCouponId(orderParam.getCouponId());
            order.setCouponAmount(calcCouponAmount(orderItemList));
        } else {
            order.setCouponAmount(BigDecimal.ZERO);
        }
        //设置用户使用积分后，总共优惠多少金额
        if (orderParam.getUseIntegration() != null) {
            order.setIntegrationAmount(calcIntegrationAmount(orderItemList));
            order.setUseIntegration(orderParam.getUseIntegration());
        } else {
            order.setIntegrationAmount(BigDecimal.ZERO);
        }
        //设置订单实际应支付的金额
        order.setPayAmount(calcPayAmount(order));
        //计算赠送的积分
        order.setIntegration(calcIntegration(orderItemList));

        //计算赠送的成长值
        order.setGrowth(calcGrowth(orderItemList));

        //生成订单号

        //设置自动收获天数
        OmsOrderSetting orderSetting = orderSettingService.getById(1l);
        order.setAutoConfirmDay(orderSetting.getConfirmOvertime());

        //插入order表和order_item表
        orderService.save(order);
        orderItemList.forEach(orderItem -> {
            orderItem.setOrderId(order.getId());
            orderItem.setOrderSn(order.getOrderSn());
        });
        orderItemService.saveBatch(orderItemList);

        //如果使用了优惠价更新优惠卷状态
        if (orderParam.getCouponId() != null) {
            LambdaQueryWrapper<SmsCouponHistory> historyLambdaQueryWrapper = new LambdaQueryWrapper<>();
            historyLambdaQueryWrapper.eq(SmsCouponHistory::getCouponId, orderParam.getCouponId());
            historyLambdaQueryWrapper.eq(SmsCouponHistory::getMemberId, member.getId());
            List<SmsCouponHistory> couponHistoryList = couponHistoryService.list(historyLambdaQueryWrapper);
            if (CollectionUtils.isNotEmpty(couponHistoryList)) {
                //将第一张优惠价的使用状态，更改为为已使用
                SmsCouponHistory smsCouponHistory = couponHistoryList.get(0);
                smsCouponHistory.setUseStatus(1);
                smsCouponHistory.setOrderId(order.getId());
                couponHistoryService.updateById(smsCouponHistory);
            }
        }


        //如果使用积分，需要扣除用户的积分
        if (orderParam.getUseIntegration() != null) {

            member.setIntegration(member.getIntegration() - orderParam.getUseIntegration());
            iUmsMemberService.updateById(member);
        }

        //删除购物车中的下单商品
        cartItemService.removeByIds(orderParam.getCarIds());

        //发送延迟消息取消订单
        Integer flashOrderOvertime = orderSetting.getFlashOrderOvertime();//获取订单的延迟时间
        cancelOrderSender.sendMessage(order.getId(), 100000); //发送延迟消息
//        cancelOrderSender.sendMessage(order.getId(),flashOrderOvertime*60*1000); //发送延迟消息


        // 将order和 orderItemList设置到 Map返回
        HashMap<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("orderItemList", orderItemList);

        return result;
    }

    @Override
    public void cancelOrder(Long orderId) {
        OmsOrder order = orderService.getById(orderId);
        Integer orderStatus = order.getStatus();
        //如果订单为待付款状态
        if (orderStatus.equals(0)) {
            order.setStatus(4);//设置订单状态取消
            orderService.updateById(order);

            // 修改优惠价使用状态
            if (order.getCouponId() != null) {
                //查找用户的第一张优惠价
                LambdaQueryWrapper<SmsCouponHistory> historyLambdaQueryWrapper = new LambdaQueryWrapper<>();
                historyLambdaQueryWrapper.eq(SmsCouponHistory::getCouponId, order.getCouponId());
                historyLambdaQueryWrapper.eq(SmsCouponHistory::getMemberId, order.getMemberId());
                historyLambdaQueryWrapper.eq(SmsCouponHistory::getUseStatus, 1);//已使用
                List<SmsCouponHistory> list = couponHistoryService.list(historyLambdaQueryWrapper);
                if (CollectionUtils.isNotEmpty(list)) {
                    SmsCouponHistory couponHistory = list.get(0);
                    couponHistory.setUseStatus(0);//设置为未使用
                    couponHistoryService.updateById(couponHistory);
                }

            }
            // 还原库存
            LambdaQueryWrapper<OmsOrderItem> orderItemLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderItemLambdaQueryWrapper.eq(OmsOrderItem::getOrderId, orderId);
            List<OmsOrderItem> orderItems = orderItemService.list(orderItemLambdaQueryWrapper);
            if (CollectionUtils.isNotEmpty(orderItems)) {
                reductionSkuStock(orderItems);
            }

            //还原用户积分
            if (order.getUseIntegration() != null) {
                UmsMember member = iUmsMemberService.getById(order.getMemberId());
                member.setIntegration(member.getIntegration() + order.getUseIntegration());
                member.setId(order.getMemberId());
                iUmsMemberService.updateById(member);
            }

        }
    }

    /**
     * 对库存进行回滚
     *
     * @param orderItems
     */
    private void reductionSkuStock(List<OmsOrderItem> orderItems) {
        for (OmsOrderItem orderItem : orderItems) {
            PmsSkuStock skuStock = iPmsSkuStockService.getById(orderItem.getProductSkuId());
            skuStock.setLockStock(skuStock.getLockStock() - orderItem.getProductQuantity());
            iPmsSkuStockService.updateById(skuStock);
        }
    }

    private Integer calcGrowth(List<OmsOrderItem> orderItemList) {
        Integer totalGrowth = 0;
        for (OmsOrderItem orderItem : orderItemList) {
            totalGrowth += orderItem.getGiftGrowth();
        }
        return totalGrowth;
    }

    private Integer calcIntegration(List<OmsOrderItem> orderItemList) {
        Integer totalIntegration = 0;
        for (OmsOrderItem orderItem : orderItemList) {
            totalIntegration += orderItem.getGiftIntegration();
        }
        return totalIntegration;
    }

    private BigDecimal calcPayAmount(OmsOrder order) {
        //实际支付金额=总金额+运费- (促销优惠-优惠价金额-积分抵扣金额)
        BigDecimal payAmount = BigDecimal.ZERO;
        BigDecimal add = order.getPromotionAmount().add(order.getCouponAmount()).add(order.getIntegrationAmount());
        payAmount = order.getTotalAmount().add(order.getFreightAmount()).subtract(add);
        return payAmount;
    }

    private BigDecimal calcIntegrationAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal totalAmoumt = BigDecimal.ZERO;
        for (OmsOrderItem orderItem : orderItemList) {
            totalAmoumt = totalAmoumt.add(orderItem.getIntegrationAmount().multiply(new BigDecimal(orderItem.getProductQuantity())));
        }
        return totalAmoumt;
    }

    /**
     * 计算订单总共的优惠卷减免金额
     *
     * @param orderItemList
     * @return
     */
    private BigDecimal calcCouponAmount(List<OmsOrderItem> orderItemList) {
        //设置优惠金额
        BigDecimal totalAmoumt = BigDecimal.ZERO;
        for (OmsOrderItem orderItem : orderItemList) {
            totalAmoumt = totalAmoumt.add(orderItem.getCouponAmount().multiply(new BigDecimal(orderItem.getProductQuantity())));
        }
        return totalAmoumt;
    }

    /**
     * 设置每个订单商品的促销活动信息
     *
     * @param orderItemList
     * @return
     */
    private String getOrderPromotionInfo(List<OmsOrderItem> orderItemList) {

        StringBuilder sb = new StringBuilder();

        for (OmsOrderItem orderItem : orderItemList) {
            sb.append(orderItem.getPromotionName());
            sb.append(";");
        }
        String result = sb.toString();
        if (result.endsWith(";")) {
            result = result.substring(0, result.length() - 1);
        }

        return result;
    }

    /**
     * 计算订单促销优惠金额
     *
     * @param orderItemList
     * @return
     */
    private BigDecimal calcPromotionAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal totalAmoumt = BigDecimal.ZERO;
        for (OmsOrderItem orderItem : orderItemList) {
            totalAmoumt = totalAmoumt.add(orderItem.getPromotionAmount().multiply(orderItem.getPromotionAmount()));
        }
        return totalAmoumt;
    }

    /**
     * 该商品经过 促销,优惠卷，积分等，优惠后的分解金额
     *
     * @param orderItemList
     */
    private void handleRealAmount(List<OmsOrderItem> orderItemList) {
        for (OmsOrderItem orderItem : orderItemList) {
            // 促销,优惠卷，积分 加起来的优惠金额
            BigDecimal decimal = orderItem.getPromotionAmount().add(orderItem.getCouponAmount()).add(orderItem.getIntegrationAmount());
            // 原价-所有优惠价
            BigDecimal realAmount = orderItem.getProductPrice().subtract(decimal);
            orderItem.setRealAmount(realAmount);
        }
    }

    /**
     * 对优惠卷 进行order_item列表的优惠处理
     *
     * @param orderItemList       order_item列表
     * @param couponHistoryDetail 优惠卷详情
     */
    private void handleCouponAmount(List<OmsOrderItem> orderItemList, SmsCouponHistoryDetail couponHistoryDetail) {
        SmsCoupon coupon = couponHistoryDetail.getCoupon();
        //过滤出订单中可优惠的商品
        if (coupon.getUseType().equals(1)) {
            //如果是指定分类
            List<Long> productCategorIds = couponHistoryDetail.getCategoryRelationList().stream().map(item -> item.getProductCategoryId()).collect(Collectors.toList());

            //对可以使用优惠卷的order_item,设置优惠金额
            //计算分摊的优惠金额
            BigDecimal amount = coupon.getAmount();//优惠卷减免金额
            Integer couponCount = 0;
            for (OmsOrderItem orderItem : orderItemList) {
                if (productCategorIds.contains(orderItem.getProductCategoryId())) {
                    couponCount += orderItem.getProductQuantity();
                }
            }
            BigDecimal couponAmount = amount.divide(new BigDecimal(couponCount), 2, RoundingMode.DOWN);
            for (OmsOrderItem orderItem : orderItemList) {
                if (productCategorIds.contains(orderItem.getProductCategoryId())) {
                    orderItem.setCouponAmount(couponAmount);
                }
            }

        } else if (coupon.getUseType().equals(2)) {
            List<Long> productIds = couponHistoryDetail.getProductRelationList().stream().map(item -> item.getProductId()).collect(Collectors.toList());
            //找出有多少个可用优惠减免的订单商品
            int couponCount = 0;
            for (OmsOrderItem orderItem : orderItemList) {
                if (productIds.contains(orderItem.getProductId())) {
                    couponCount += orderItem.getProductQuantity();
                }
            }

            BigDecimal amount = couponHistoryDetail.getCoupon().getAmount();//优惠卷价格
            BigDecimal couponAmount = amount.divide(new BigDecimal(couponCount), 2, RoundingMode.DOWN); //计算每件商品分摊多少优惠
            //设置商品的优惠价格
            for (OmsOrderItem orderItem : orderItemList) {
                //如果该订单商品在优惠卷中存在
                if (productIds.contains(orderItem.getProductId())) {
                    //设置订单商品的优惠减免金额
                    orderItem.setCouponAmount(couponAmount);
                }
            }

        } else {
            //如果是全场通用优惠卷
            //获取所有商品的数量
            int count = 0;
            for (OmsOrderItem orderItem : orderItemList) {
                count += orderItem.getProductQuantity();
            }
            BigDecimal couponAmount = coupon.getAmount().divide(new BigDecimal(count), 2, RoundingMode.DOWN);
            for (OmsOrderItem orderItem : orderItemList) {
                orderItem.setCouponAmount(couponAmount);
            }
        }
    }


    /**
     * 判断每件商品的库存是否充足
     *
     * @param cartPromotionItemList
     * @return
     */
    private boolean hasStock(List<CartPromotionItem> cartPromotionItemList) {

        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            if (cartPromotionItem.getRealStock() == null || cartPromotionItem.getRealStock() <= 0) {
                return false;
            }

        }

        return true;
    }


}

