package com.itheima.mall.dao;

import com.itheima.mall.dto.PromotionProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 前台购物车商品管理自定义Dao
 * Created by macro on 2018/8/2.
 */
@Mapper
public interface PortalProductDao {


    /**
     * 获取促销商品信息列表
     */
    List<PromotionProduct> getPromotionProductList(@Param("ids") List<Long> ids);


}