package com.itheima.mall.service.impl;

import com.itheima.mall.domain.PmsProductCategoryAttributeRelation;
import com.itheima.mall.dao.PmsProductCategoryAttributeRelationDao;
import com.itheima.mall.service.IPmsProductCategoryAttributeRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 产品的分类和属性的关系表，用于设置分类筛选条件（只支持一级分类） 服务实现类
 * </p>
 *
 * @author 小刘
 * @since 2022-11-17
 */
@Service
public class PmsProductCategoryAttributeRelationServiceImpl extends ServiceImpl<PmsProductCategoryAttributeRelationDao, PmsProductCategoryAttributeRelation> implements IPmsProductCategoryAttributeRelationService {

}
