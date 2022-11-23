package com.itheima.mall.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.itheima.mall.domain.*;
import com.itheima.mall.dto.PmsProductParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class AdminProductService {
    @Autowired
    private IPmsProductService iPmsProductService;

    @Autowired
    private IPmsMemberPriceService iPmsMemberPriceService;

    @Autowired
    private IPmsProductFullReductionService iPmsProductFullReductionService;

    @Autowired
    private IPmsProductLadderService iPmsProductLadderService;

    @Autowired
    private IPmsSkuStockService iPmsSkuStockService;

    @Autowired
    private ICmsPrefrenceAreaProductRelationService iCmsPrefrenceAreaProductRelationService;
    @Autowired
    private ICmsSubjectProductRelationService iCmsSubjectProductRelationService;

    @Autowired
    private IPmsProductAttributeValueService iPmsProductAttributeValueService;


    @Transactional
    public void create(PmsProductParam pmsProductParam) {
        // 商品基础数据
        PmsProduct pmsProduct = pmsProductParam;
        iPmsProductService.save(pmsProduct);

        Long productId = pmsProduct.getId();

        // 商品会员
        insertList(iPmsMemberPriceService, pmsProductParam.getMemberPriceList(), productId);


        //阶梯价格
        insertList(iPmsProductLadderService, pmsProductParam.getProductLadderList(), productId);

        //满减价格
        insertList(iPmsProductFullReductionService, pmsProductParam.getProductFullReductionList(), productId);

        //sku 库存设置
        insertList(iPmsSkuStockService, pmsProductParam.getSkuStockList(), productId);

        //商品参数
        insertList(iPmsProductAttributeValueService, pmsProductParam.getProductAttributeValueList(), productId);

        //专题和商品关系
        insertList(iCmsSubjectProductRelationService, pmsProductParam.getSubjectProductRelationList(), productId);

        //优选专区和商品的关系
        insertList(iCmsPrefrenceAreaProductRelationService, pmsProductParam.getPrefrenceAreaProductRelationList(), productId);


    }


    private void insertList(Object service, List dataList, Long productId) {
        // 通过反射调用 集合中的每一个对象的set方法设置属性
        try {
            if (CollectionUtils.isEmpty(dataList)) return;
            for (Object item : dataList) {
                Class<?> c = item.getClass();
                Method setProductId = c.getMethod("setProductId", Long.class);
                setProductId.invoke(item, productId);


            }
            Method saveBatch = service.getClass().getMethod("saveBatch", Collection.class);
            saveBatch.invoke(service, dataList);


        } catch (Exception e) {
            log.warn("创建产品出错:{}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }


    }

    public PmsProductParam getById(Long id) {
        //商品基础信息
        PmsProduct pmsProduct = iPmsProductService.getById(id);
        PmsProductParam pmsProductParam = new PmsProductParam();
        BeanUtils.copyProperties(pmsProduct, pmsProductParam);

        LambdaQueryWrapper<PmsMemberPrice> pmsMemberPriceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        pmsMemberPriceLambdaQueryWrapper.eq(PmsMemberPrice::getProductId, id);
        // 商品会员
        List<PmsMemberPrice> memberPriceList = iPmsMemberPriceService.list(pmsMemberPriceLambdaQueryWrapper);


        //商品参数
        LambdaQueryWrapper<PmsProductAttributeValue> pmsProductAttributeValueLambdaQueryWrapper = new LambdaQueryWrapper<>();
        pmsProductAttributeValueLambdaQueryWrapper.eq(PmsProductAttributeValue::getProductId, id);
        List<PmsProductAttributeValue> productAttributeValueList = iPmsProductAttributeValueService.list(pmsProductAttributeValueLambdaQueryWrapper);


        //阶梯价格
        LambdaQueryWrapper<PmsProductLadder> pmsProductLadderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        pmsProductLadderLambdaQueryWrapper.eq(PmsProductLadder::getProductId, id);
        List<PmsProductLadder> productLadderList = iPmsProductLadderService.list(pmsProductLadderLambdaQueryWrapper);

        //满减价格
        LambdaQueryWrapper<PmsProductFullReduction> pmsProductFullReductionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        pmsProductFullReductionLambdaQueryWrapper.eq(PmsProductFullReduction::getProductId, id);
        List<PmsProductFullReduction> productFullReductionList = iPmsProductFullReductionService.list(pmsProductFullReductionLambdaQueryWrapper);

        //sku 库存设置
        LambdaQueryWrapper<PmsSkuStock> pmsSkuStockLambdaQueryWrapper = new LambdaQueryWrapper<>();
        pmsSkuStockLambdaQueryWrapper.eq(PmsSkuStock::getProductId, id);
        List<PmsSkuStock> skuStockList = iPmsSkuStockService.list(pmsSkuStockLambdaQueryWrapper);


        //专题和商品关系
        LambdaQueryWrapper<CmsSubjectProductRelation> cmsSubjectProductRelationLambdaQueryWrapper = new LambdaQueryWrapper<>();
        cmsSubjectProductRelationLambdaQueryWrapper.eq(CmsSubjectProductRelation::getProductId, id);
        List<CmsSubjectProductRelation> subjectProductRelationList = iCmsSubjectProductRelationService.list(cmsSubjectProductRelationLambdaQueryWrapper);

        //优选专区和商品的关系
        LambdaQueryWrapper<CmsPrefrenceAreaProductRelation> cmsPrefrenceAreaProductRelationLambdaQueryWrapper = new LambdaQueryWrapper<>();
        cmsPrefrenceAreaProductRelationLambdaQueryWrapper.eq(CmsPrefrenceAreaProductRelation::getProductId, id);
        List<CmsPrefrenceAreaProductRelation> prefrenceAreaProductRelationList = iCmsPrefrenceAreaProductRelationService.list(cmsPrefrenceAreaProductRelationLambdaQueryWrapper);


        pmsProductParam.setMemberPriceList(memberPriceList);
        pmsProductParam.setProductLadderList(productLadderList);
        pmsProductParam.setProductFullReductionList(productFullReductionList);
        pmsProductParam.setSkuStockList(skuStockList);
        pmsProductParam.setSubjectProductRelationList(subjectProductRelationList);
        pmsProductParam.setPrefrenceAreaProductRelationList(prefrenceAreaProductRelationList);
        pmsProductParam.setProductAttributeValueList(productAttributeValueList);
        return pmsProductParam;
    }

    @Transactional
    public void update(PmsProductParam pmsProductParam) {
        Long productId = pmsProductParam.getId();
        PmsProduct pmsProduct = new PmsProduct();
        BeanUtils.copyProperties(pmsProductParam, pmsProduct);

        //修改商品基础信息
        iPmsProductService.updateById(pmsProduct);

        //修改商品会员信息
        LambdaUpdateWrapper<PmsMemberPrice> pmsMemberPriceLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        pmsMemberPriceLambdaUpdateWrapper.eq(PmsMemberPrice::getProductId, productId);
        iPmsMemberPriceService.remove(pmsMemberPriceLambdaUpdateWrapper);

        insertList(iPmsMemberPriceService, pmsProductParam.getMemberPriceList(), productId);


        //阶梯价格

        LambdaUpdateWrapper<PmsProductLadder> pmsProductLadderLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        pmsProductLadderLambdaUpdateWrapper.eq(PmsProductLadder::getProductId, productId);
        iPmsProductLadderService.remove(pmsProductLadderLambdaUpdateWrapper);
        insertList(iPmsProductLadderService, pmsProductParam.getProductLadderList(), productId);

        //满减价格
        LambdaUpdateWrapper<PmsProductFullReduction> pmsProductFullReductionLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        pmsProductFullReductionLambdaUpdateWrapper.eq(PmsProductFullReduction::getProductId, productId);
        iPmsProductFullReductionService.remove(pmsProductFullReductionLambdaUpdateWrapper);
        insertList(iPmsProductFullReductionService, pmsProductParam.getProductFullReductionList(), productId);

        //sku 库存设置
        LambdaUpdateWrapper<PmsSkuStock> pmsSkuStockLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        pmsSkuStockLambdaUpdateWrapper.eq(PmsSkuStock::getProductId, productId);
        iPmsSkuStockService.remove(pmsSkuStockLambdaUpdateWrapper);
        insertList(iPmsSkuStockService, pmsProductParam.getSkuStockList(), productId);

        //商品参数
        LambdaUpdateWrapper<PmsProductAttributeValue> pmsProductAttributeValueLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        pmsProductAttributeValueLambdaUpdateWrapper.eq(PmsProductAttributeValue::getProductId, productId);
        iPmsProductAttributeValueService.remove(pmsProductAttributeValueLambdaUpdateWrapper);
        insertList(iPmsProductAttributeValueService, pmsProductParam.getProductAttributeValueList(), productId);

        //专题和商品关系
        LambdaUpdateWrapper<CmsSubjectProductRelation> productRelationLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        productRelationLambdaUpdateWrapper.eq(CmsSubjectProductRelation::getProductId, productId);
        iCmsSubjectProductRelationService.remove(productRelationLambdaUpdateWrapper);
        insertList(iCmsSubjectProductRelationService, pmsProductParam.getSubjectProductRelationList(), productId);

        //优选专区和商品的关系
        LambdaUpdateWrapper<CmsPrefrenceAreaProductRelation> prefrenceAreaProductRelationLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        prefrenceAreaProductRelationLambdaUpdateWrapper.eq(CmsPrefrenceAreaProductRelation::getProductId, productId);
        iCmsPrefrenceAreaProductRelationService.remove(prefrenceAreaProductRelationLambdaUpdateWrapper);
        insertList(iCmsPrefrenceAreaProductRelationService, pmsProductParam.getPrefrenceAreaProductRelationList(), productId);


    }
}

