package com.itheima.mall.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.mall.domain.*;
import com.itheima.mall.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class portalProductServiceImpl implements PmsPortalProductService {
    @Autowired
    private IPmsProductService iPmsProductService;
    @Autowired
    private IPmsSkuStockService iPmsSkuStockService;
    @Autowired
    private IPmsProductAttributeValueService iPmsProductAttributeValueService;
    @Autowired
    private IPmsProductAttributeService iPmsProductAttributeService;
    @Autowired
    private IPmsBrandService iPmsBrandService;

    @Override
    public PmsPortalProductDetail detail(Long id) {
        //商品基础信息
        PmsProduct pmsProduct = iPmsProductService.getById(id);
        //商品sku规格
        LambdaQueryWrapper<PmsSkuStock> pmsSkuStockLambdaQueryWrapper = new LambdaQueryWrapper<>();
        pmsSkuStockLambdaQueryWrapper.eq(PmsSkuStock::getProductId, id);
        List<PmsSkuStock> skuStockList = iPmsSkuStockService.list(pmsSkuStockLambdaQueryWrapper);

        //商品属性值
        LambdaQueryWrapper<PmsProductAttribute> pmsProductAttributeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        pmsProductAttributeLambdaQueryWrapper.eq(PmsProductAttribute::getProductAttributeCategoryId, pmsProduct.getProductAttributeCategoryId());
        pmsProductAttributeLambdaQueryWrapper.eq(PmsProductAttribute::getType, 0);
        List<PmsProductAttribute> productAttributeList = iPmsProductAttributeService.list(pmsProductAttributeLambdaQueryWrapper);


        //商品参数
        LambdaQueryWrapper<PmsProductAttributeValue> pmsProductAttributeValueLambdaQueryWrapper = new LambdaQueryWrapper<>();
        pmsProductAttributeValueLambdaQueryWrapper.eq(PmsProductAttributeValue::getProductId, id);
        List<PmsProductAttributeValue> productAttributeValueList = iPmsProductAttributeValueService.list(pmsProductAttributeValueLambdaQueryWrapper);
        //商品品牌
        LambdaQueryWrapper<PmsBrand> pmsBrandLambdaQueryWrapper = new LambdaQueryWrapper<>();
        pmsBrandLambdaQueryWrapper.eq(PmsBrand::getId, pmsProduct.getBrandId());
        PmsBrand pmsBrand = iPmsBrandService.getOne(pmsBrandLambdaQueryWrapper);

        PmsPortalProductDetail pmsPortalProductDetail = new PmsPortalProductDetail();
        pmsPortalProductDetail.setPmsProduct(pmsProduct);
        pmsPortalProductDetail.setPmsSkuStockList(skuStockList);
        pmsPortalProductDetail.setProductAttributeValueList(productAttributeValueList);
        pmsPortalProductDetail.setPmsBrand(pmsBrand);
        pmsPortalProductDetail.setProductAttributeList(productAttributeList);

        return pmsPortalProductDetail;
    }
}
