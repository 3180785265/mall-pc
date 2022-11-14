package com.itheima.mall.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.mall.domain.PmsBrand;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 品牌表 Mapper 接口
 * </p>
 *
 * @author 小刘
 * @since 2022-11-14
 */
public interface PmsBrandDao extends BaseMapper<PmsBrand> {

    IPage<PmsBrand>  recommendBrand(@Param("page") Page<PmsBrand> page);
}
