package com.itheima.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.itheima.mall.dao.PortalProductDao;
import com.itheima.mall.domain.*;
import com.itheima.mall.dto.PromotionProduct;
import com.itheima.mall.service.IPmsMemberPriceService;
import com.itheima.mall.service.IUmsMemberService;
import com.itheima.mall.service.OmsPromotionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class OmsPromotionServiceImpl implements OmsPromotionService {
    @Autowired
    private PortalProductDao portalProductDao;
    @Autowired
    private IUmsMemberService iUmsMemberService;
    @Autowired
    private IPmsMemberPriceService iPmsMemberPriceService;

    @Override
    public List<CartPromotionItem> calcCartPromotion(List<OmsCartItem> cartItemList) {

        //1.查询所有商品的促销信息
        List<Long> productIds = cartItemList.stream().map(cartItem -> cartItem.getProductId()).collect(Collectors.toList());
        List<PromotionProduct> promotionProductList = getPromotionProductList(productIds);//(包括了商品spu，商品sku，商品折扣,商品满减)


        //2.1先根据productId对CartItem进行分组，以spu为单位进行计算优惠
        Map<Long, List<OmsCartItem>> productCartMap = groupCartItemBySpu(cartItemList);


        //2.2根据商品促销类型计算商品促销优惠价格
        List<CartPromotionItem> cartPromotionItemList = new ArrayList<>();
        for (Map.Entry<Long, List<OmsCartItem>> entry : productCartMap.entrySet()) {

            Long productId = entry.getKey();
            List<OmsCartItem> itemList = entry.getValue();

            //2.2.1 根据商品id，找出对应的促销策略
            PromotionProduct promotionProduct = getPromotionProductById(productId, promotionProductList);

            // 2.2.2根据不同的商品促销类型，封装购物车商品的优惠金额
            if (promotionProduct.getPromotionType() == 1) {
                for (OmsCartItem cartItem : itemList) {
                    //找出对应的sku
                    PmsSkuStock skuStock = getSkuProduct(promotionProduct.getSkuStockList(), productId);
                    BigDecimal reduceAmount = skuStock.getPrice().subtract(skuStock.getPromotionPrice());

                    CartPromotionItem cartPromotionItem = new CartPromotionItem();
                    BeanUtils.copyProperties(cartItem, cartPromotionItem);
                    StringBuilder sb = new StringBuilder();
                    sb.append("促销价:");
                    sb.append(skuStock.getPrice());
                    sb.append("元");
                    sb.append("减");
                    sb.append(reduceAmount);
                    sb.append("元");
                    cartPromotionItem.setPromotionMessage(sb.toString());
                    cartPromotionItem.setReduceAmount(reduceAmount);//减免金额= 原价-促销金额
                    cartPromotionItem.setRealStock(skuStock.getStock() - skuStock.getLockStock());
                    cartPromotionItem.setIntegration(promotionProduct.getGiftPoint());
                    cartPromotionItem.setGrowth(promotionProduct.getGiftGrowth());
                    cartPromotionItem.setCouponPrice(skuStock.getPromotionPrice());
                    //设置商品销售属性
                    cartPromotionItem.setProductAttr(skuStock.getSpData());

                }
            }
            //会员价格
            else if (promotionProduct.getPromotionType() == 2) {
                UmsMember member = iUmsMemberService.getById(1l);
                Long memberLevelId = member.getMemberLevelId();
                //根据对应的会员，查询会员优惠价格
                PmsMemberPrice memberPrice = iPmsMemberPriceService.getOne(new LambdaQueryWrapper<PmsMemberPrice>().eq(PmsMemberPrice::getMemberLevelId, memberLevelId));
                PmsSkuStock skuStock = getSkuProduct(promotionProduct.getSkuStockList(), productId);
                // 优惠价格= 原价-会员优惠价格

                if (memberPrice != null) {
                    for (OmsCartItem cartItem : itemList) {

                        BigDecimal reduceAmount = skuStock.getPrice().subtract(memberPrice.getMemberPrice());

                        CartPromotionItem cartPromotionItem = new CartPromotionItem();
                        BeanUtils.copyProperties(cartItem, cartPromotionItem);
                        StringBuilder sb = new StringBuilder();
                        sb.append("会员:");
                        sb.append(skuStock.getPrice());
                        sb.append(memberPrice.getMemberLevelName());
                        sb.append("减");
                        sb.append(reduceAmount);
                        sb.append("元");
                        cartPromotionItem.setPromotionMessage(sb.toString());
                        cartPromotionItem.setReduceAmount(reduceAmount);//减免金额= 原价-促销金额
                        cartPromotionItem.setRealStock(skuStock.getStock() - skuStock.getLockStock());
                        cartPromotionItem.setIntegration(promotionProduct.getGiftPoint());
                        cartPromotionItem.setGrowth(promotionProduct.getGiftGrowth());
                        cartPromotionItem.setCouponPrice(skuStock.getPromotionPrice());
                    }

                } else {
                    //按照无促销逻辑处理
                    handleNoReduce(cartPromotionItemList, itemList, promotionProduct);
                }


            }
            //折扣价格
            else if (promotionProduct.getPromotionType() == 3) {
                //找出对应的折扣配置
                List<PmsProductLadder> productLadderList = promotionProduct.getProductLadderList();
                Integer quantity = itemList.stream().map(item -> item.getQuantity()).reduce((a, b) -> a + b).get();
                productLadderList.sort((PmsProductLadder p1, PmsProductLadder p2) -> {
                    return p2.getCount() - p1.getCount();
                });
                List<PmsProductLadder> productLadders = productLadderList.stream().filter(item -> item.getCount() <= quantity).collect(Collectors.toList());
                //如果没有折扣
                if (CollectionUtils.isNotEmpty(productLadders)) {
                    PmsProductLadder pmsProductLadder = productLadders.get(0);
                    for (OmsCartItem cartItem : itemList) {
                        //计算优惠价格( sku原价-(原价*折扣) )
                        PmsSkuStock skuStock = getSkuProduct(promotionProduct.getSkuStockList(), cartItem.getProductSkuId());
                        BigDecimal originalPrice = skuStock.getPrice();
                        BigDecimal reduceAmount = originalPrice.subtract(originalPrice.multiply(pmsProductLadder.getDiscount()));// 优惠多少钱
                        CartPromotionItem cartPromotionItem = new CartPromotionItem();
                        BeanUtils.copyProperties(cartItem, cartPromotionItem);
                        StringBuilder sb = new StringBuilder();
                        sb.append("折扣优惠价:");
                        sb.append("满:");
                        sb.append(pmsProductLadder.getCount());
                        sb.append("件");
                        sb.append("优惠:");
                        sb.append(reduceAmount);
                        sb.append("元");
                        cartPromotionItem.setPromotionMessage(sb.toString());
                        cartPromotionItem.setReduceAmount(reduceAmount);//减免金额= 原价-促销金额
                        cartPromotionItem.setRealStock(skuStock.getStock() - skuStock.getLockStock());
                        cartPromotionItem.setIntegration(promotionProduct.getGiftPoint());
                        cartPromotionItem.setGrowth(promotionProduct.getGiftGrowth());
                        cartPromotionItem.setCouponPrice(originalPrice.multiply(pmsProductLadder.getDiscount()));
                        cartPromotionItemList.add(cartPromotionItem);
                    }
                } else {
                    //按照无促销逻辑处理
                    handleNoReduce(cartPromotionItemList, itemList, promotionProduct);
                }

            } else if (promotionProduct.getPromotionType() == 4) {
                //获取所有sku商品的累计金额
                BigDecimal totalAmount = getCartItemAmount(itemList, promotionProductList);
                //根据sku的合计价格，到满减表中，查出对应的满减
                PmsProductFullReduction fullReduction = getProductFullReduction(totalAmount, promotionProduct.getProductFullReductionList());
                if (fullReduction != null) {
                    for (OmsCartItem cartItem : entry.getValue()) {

                        //计算每个商品平均优惠多少金额
                        //(商品原价/总价)*满减金额
                        PmsSkuStock skuStock = getSkuProduct(promotionProduct.getSkuStockList(), cartItem.getProductSkuId());
                        BigDecimal originalPrice = skuStock.getPrice();
                        BigDecimal reduceAmount = originalPrice.divide(totalAmount, 2, RoundingMode.DOWN)
                                .multiply(fullReduction.getReducePrice());

                        CartPromotionItem cartPromotionItem = new CartPromotionItem();
                        //设置优惠金额
                        cartPromotionItem.setReduceAmount(reduceAmount);
                        //设置优惠后的价格
                        BeanUtils.copyProperties(cartItem, cartPromotionItem);

                        StringBuilder sb = new StringBuilder();
                        sb.append("满减优惠：");
                        sb.append("满");
                        sb.append(fullReduction.getFullPrice());
                        sb.append("元，");
                        sb.append("减");
                        sb.append(fullReduction.getReducePrice());
                        sb.append("元");
                        cartPromotionItem.setPromotionMessage(sb.toString());
                        cartPromotionItem.setReduceAmount(reduceAmount);
                        cartPromotionItem.setRealStock(skuStock.getStock() - skuStock.getLockStock());
                        cartPromotionItem.setIntegration(promotionProduct.getGiftPoint());
                        cartPromotionItem.setGrowth(promotionProduct.getGiftGrowth());
                        cartPromotionItem.setCouponPrice(originalPrice.subtract(reduceAmount));

                        cartPromotionItemList.add(cartPromotionItem);
                    }

                } else {
                    //按照无促销逻辑处理
                    handleNoReduce(cartPromotionItemList, itemList, promotionProduct);
                }
            } else {
                //按照无促销逻辑处理
                handleNoReduce(cartPromotionItemList, itemList, promotionProduct);
            }

        }


        return cartPromotionItemList;
    }

    private void handleNoReduce(List<CartPromotionItem> cartPromotionItemList, List<OmsCartItem> itemList, PromotionProduct promotionProduct) {
        for (OmsCartItem cartItem : itemList) {
            PmsSkuStock skuStock = getSkuProduct(promotionProduct.getSkuStockList(), cartItem.getProductSkuId());
            BigDecimal originalPrice = skuStock.getPrice();
            BigDecimal reduceAmount = new BigDecimal("0");
            CartPromotionItem cartPromotionItem = new CartPromotionItem();
            BeanUtils.copyProperties(cartItem, cartPromotionItem);
            StringBuilder sb = new StringBuilder();
            sb.append("无优惠:");
            cartPromotionItem.setPromotionMessage(sb.toString());
            cartPromotionItem.setReduceAmount(reduceAmount);//减免金额= 原价-促销金额
            cartPromotionItem.setRealStock(skuStock.getStock() - skuStock.getLockStock());
            cartPromotionItem.setIntegration(promotionProduct.getGiftPoint());
            cartPromotionItem.setGrowth(promotionProduct.getGiftGrowth());
            cartPromotionItem.setCouponPrice(originalPrice);
            cartPromotionItemList.add(cartPromotionItem);
        }
    }

    private PmsProductFullReduction getProductFullReduction(BigDecimal totalAmount, List<PmsProductFullReduction> productFullReductionList) {

        //对根据满减的价格，从搞到底进行排序排序
        productFullReductionList.sort((PmsProductFullReduction p1, PmsProductFullReduction p2) -> {
            return p2.getFullPrice().subtract(p1.getFullPrice()).intValue();
        });

        //如果总价，大于满减集合中的价格，直接返回满减对象
        for (PmsProductFullReduction pmsProductFullReduction : productFullReductionList) {
            if (totalAmount.doubleValue() > pmsProductFullReduction.getFullPrice().doubleValue()) {
                return pmsProductFullReduction;
            }
        }
        return null;
    }

    /**
     * 计算购物车中指定商品的总价
     * sku商品总价
     *
     * @param itemList
     * @param promotionProductList
     * @return
     */
    private BigDecimal getCartItemAmount(List<OmsCartItem> itemList, List<PromotionProduct> promotionProductList) {
        BigDecimal amount = new BigDecimal("0");//所有sku商品总价
        //循环累计所有sku商品的总价
        for (OmsCartItem omsCartItem : itemList) {
            //找出商品对应的优惠
            PromotionProduct promotionProduct = getPromotionProductById(omsCartItem.getProductId(), promotionProductList);
            //获取商品的sku价格
            PmsSkuStock skuStock = getSkuProduct(promotionProduct.getSkuStockList(), omsCartItem.getProductSkuId());
            BigDecimal originalPrice = skuStock.getPrice();
            //累计商品的sku价格
            amount = amount.add(originalPrice.multiply(new BigDecimal(omsCartItem.getQuantity())));
        }
        return amount;
    }

    /**
     * 找出商品对应的sku
     *
     * @return
     */
    private PmsSkuStock getSkuProduct(List<PmsSkuStock> skuStockList, Long productSkuId) {
        PmsSkuStock pmsSkuStock = null;
        for (PmsSkuStock item : skuStockList) {
            if (productSkuId.equals(item.getId())) {
                pmsSkuStock = item;
                break;
            }
        }
        return pmsSkuStock;
    }

    private List<PromotionProduct> getPromotionProductList(List<Long> productIds) {

        return portalProductDao.getPromotionProductList(productIds);
    }

    /**
     * 根据商品id，找出属于商品的促销活动信息
     */
    private PromotionProduct getPromotionProductById(Long productId, List<PromotionProduct> promotionProductList) {

        List<PromotionProduct> list = promotionProductList.stream().filter(promotionProduct -> promotionProduct.getId() == productId).collect(Collectors.toList());

        return list.get(0);

    }


    /**
     * 以spu为单位对购物车商品进行分组
     *
     * @param cartItemList
     * @return
     */
    private Map<Long, List<OmsCartItem>> groupCartItemBySpu(List<OmsCartItem> cartItemList) {


        TreeMap<Long, List<OmsCartItem>> productCartMap = new TreeMap<>();

        //循环购物车
        for (OmsCartItem cartItem : cartItemList) {
            Long productId = cartItem.getProductId();
            List<OmsCartItem> productCartItemList = productCartMap.get(productId);
            //如果不存在，创建一个k,v添加
            if (productCartItemList == null) {
                productCartItemList = new ArrayList<>();
                productCartItemList.add(cartItem);
                productCartMap.put(productId, productCartItemList);

            } else {//存在则直接往value中添加商品
                productCartItemList.add(cartItem);
            }
        }
        return productCartMap;

    }


}
