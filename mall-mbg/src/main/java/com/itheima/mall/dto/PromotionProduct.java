package com.itheima.mall.dto;

import com.itheima.mall.domain.PmsProduct;
import com.itheima.mall.domain.PmsProductFullReduction;
import com.itheima.mall.domain.PmsProductLadder;
import com.itheima.mall.domain.PmsSkuStock;
import lombok.Data;

import java.util.List;

/**
 * 促销商品信息，包括sku、打折优惠、满减优惠
 * Created by macro on 2018/8/27.
 */
@Data
public class PromotionProduct extends PmsProduct {
    //商品库存信息
    private List<PmsSkuStock> skuStockList;
    //商品打折信息
    private List<PmsProductLadder> productLadderList;
    //商品满减信息
    private List<PmsProductFullReduction> productFullReductionList;
}
