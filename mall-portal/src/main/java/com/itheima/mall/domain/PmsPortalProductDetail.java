package com.itheima.mall.domain;

import lombok.Data;

import java.util.List;

@Data
public class PmsPortalProductDetail {
    //商品信息
    private PmsProduct pmsProduct;

    //商品规格信息
    private List<PmsSkuStock> pmsSkuStockList;
    //商品属性
    private List<PmsProductAttribute> productAttributeList;
    //商品参数
    private List<PmsProductAttributeValue> productAttributeValueList;
    //商品品牌
    private PmsBrand pmsBrand;

}
