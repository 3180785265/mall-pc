package com.itheima.mall.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.itheima.mall.dao.SmsCouponHistoryDao;
import com.itheima.mall.domain.CartPromotionItem;
import com.itheima.mall.domain.SmsCouponHistoryDetail;
import com.itheima.mall.service.UmsMemberCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UmsMemberCouponServiceImpl implements UmsMemberCouponService {
    @Autowired
    private SmsCouponHistoryDao smsCouponHistoryDao;

    @Override
    public List<SmsCouponHistoryDetail> listCart(List<CartPromotionItem> cartPromotionItemList, int type) {
        LocalDateTime date = LocalDateTime.now();
        //查询用户的所有优惠卷
        Long memberId = 1l;
        List<SmsCouponHistoryDetail> smsCouponHistoryDetailList = smsCouponHistoryDao.getDetailList(memberId);
        if (CollectionUtils.isEmpty(smsCouponHistoryDetailList)) {
            return null;
        }
        //根据优惠券使用类型来判断优惠券是否可用
        List<SmsCouponHistoryDetail> enableList = new ArrayList<>();
        List<SmsCouponHistoryDetail> disableList = new ArrayList<>();
        // 从商品中，过滤出，用户可使用的优惠卷
        for (SmsCouponHistoryDetail smsCouponHistoryDetail : smsCouponHistoryDetailList) {
            //判断优惠卷是否在指定时间之内可用
            if (!date.isAfter(smsCouponHistoryDetail.getCoupon().getStartTime()) || !date.isBefore(smsCouponHistoryDetail.getCoupon().getEndTime())) {
                disableList.add(smsCouponHistoryDetail);
                continue;
            }

            //如果是全场通用类型
            if (smsCouponHistoryDetail.getCoupon().getUseType() == 0) {
                enableList.add(smsCouponHistoryDetail);
                continue;
            }
            //如果是指定商品分类
            else if (smsCouponHistoryDetail.getCoupon().getUseType() == 1) {
                //判断优惠卷是否在指定的门槛价格当中
                if (!exisCategoryMinPoint(smsCouponHistoryDetail, cartPromotionItemList)) {
                    disableList.add(smsCouponHistoryDetail);
                    continue;
                }
//                if (!existCategoryCoupon(smsCouponHistoryDetail, cartPromotionItemList)) {
//                    //判断用户购物车中的商品，是否有商品中，对应的优惠卷可以使用
//                    disableList.add(smsCouponHistoryDetail);
//                    continue;
//                }
                // 添加到可用优惠卷
                enableList.add(smsCouponHistoryDetail);
            }
            //如果是指定商品
            else if (smsCouponHistoryDetail.getCoupon().getUseType() == 2) {
                //判断优惠卷是否在指定的门槛价格当中
                if (!exisProductMinPoint(smsCouponHistoryDetail, cartPromotionItemList)) {
                    disableList.add(smsCouponHistoryDetail);
                    continue;
                }
//                if (!existProductIdsCoupon(smsCouponHistoryDetail, cartPromotionItemList)) {
//                    //判断用户购物车中的商品，是否有商品中，对应的优惠卷可以使用
//                    disableList.add(smsCouponHistoryDetail);
//                    continue;
//                }
                // 添加到可用优惠卷
                enableList.add(smsCouponHistoryDetail);

            }


        }


        if (type == 1) {
            return enableList;
        } else {
            return disableList;
        }

    }

    /**
     * 判断购物车中的指定商品，是否符合购物车的门槛
     *
     * @param smsCouponHistoryDetail
     * @param cartPromotionItemList
     * @return
     */
    private boolean exisProductMinPoint(SmsCouponHistoryDetail smsCouponHistoryDetail, List<CartPromotionItem> cartPromotionItemList) {
        BigDecimal totalAmont = new BigDecimal("0");
        //门槛价格
        BigDecimal minPoint = smsCouponHistoryDetail.getCoupon().getMinPoint();
        //判断购物车中的指定分类商品，是否符合购物车的门槛
        List<Long> productIds = smsCouponHistoryDetail.getProductRelationList().stream().map(item -> item.getProductId()).collect(Collectors.toList());
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            //如果当前购物车商品，在分类中存在
            if (productIds.contains(cartPromotionItem.getProductId())) {
                // 将价格累加 分类商品总价+=每件
                totalAmont = totalAmont.add(cartPromotionItem.getPrice().multiply(new BigDecimal(cartPromotionItem.getQuantity().toString())));
            }
        }

        if (totalAmont.subtract(minPoint).doubleValue() >= 0) {
            return true;
        }
        return false;

    }

    /**
     * 判断购物车中的指定分类商品，是否符合购物车的门槛
     *
     * @param smsCouponHistoryDetail
     * @param cartPromotionItemList
     * @return
     */
    private boolean exisCategoryMinPoint(SmsCouponHistoryDetail smsCouponHistoryDetail, List<CartPromotionItem> cartPromotionItemList) {
        BigDecimal totalAmont = new BigDecimal("0");
        //门槛价格
        BigDecimal minPoint = smsCouponHistoryDetail.getCoupon().getMinPoint();
        //判断购物车中的指定分类商品，是否符合购物车的门槛
        List<Long> categoryIds = smsCouponHistoryDetail.getCategoryRelationList().stream().map(item -> item.getProductCategoryId()).collect(Collectors.toList());
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            //如果当前购物车商品，在分类中存在
            if (categoryIds.contains(cartPromotionItem.getProductCategoryId())) {
                // 将价格累加 分类商品总价+=每件
                totalAmont = totalAmont.add(cartPromotionItem.getPrice().multiply(new BigDecimal(cartPromotionItem.getQuantity().toString())));
            }
        }

        if (totalAmont.subtract(minPoint).doubleValue() >= 0) {
            return true;
        }
        return false;

    }

    /**
     * 判断购物车中，是否有对应的商品优惠卷，如果有直接返回true
     *
     * @param smsCouponHistoryDetail
     * @param cartPromotionItemList
     * @return
     */
    private boolean existProductIdsCoupon(SmsCouponHistoryDetail smsCouponHistoryDetail, List<CartPromotionItem> cartPromotionItemList) {
        if (CollectionUtils.isEmpty(smsCouponHistoryDetail.getProductRelationList())) {
            return false;
        }
        List<Long> productIds = smsCouponHistoryDetail.getProductRelationList().stream().map(item -> item.getProductId()).collect(Collectors.toList());

        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            //如果当前购物车中的商品，有对应的优惠卷，直接返回true
            if (productIds.contains(cartPromotionItem.getProductId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断优惠卷关联的分类ID，在购物车中是否有存在，只要存在1个，直接返回true，表示当前优惠卷为用户可用
     *
     * @param smsCouponHistoryDetail
     * @param cartPromotionItemList
     * @return
     */
    private boolean existCategoryCoupon(SmsCouponHistoryDetail smsCouponHistoryDetail, List<CartPromotionItem> cartPromotionItemList) {

        List<Long> categoryIds = smsCouponHistoryDetail.getCategoryRelationList().stream().map(item -> item.getProductCategoryId()).collect(Collectors.toList());

        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {

            for (Long categoryId : categoryIds) {
                if (categoryId.equals(cartPromotionItem.getProductCategoryId())) {
                    return true;
                }
            }
        }
        return false;
    }
}
