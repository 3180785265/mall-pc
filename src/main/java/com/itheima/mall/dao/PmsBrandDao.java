package com.itheima.mall.dao;

import com.itheima.mall.domain.PmsBrand;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 品牌表 Mapper 接口
 * </p>
 *
 * @author 小刘
 * @since 2022-10-24
 */
public interface PmsBrandDao extends BaseMapper<PmsBrand> {

    List<PmsBrand> recommendBrand(@Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize);
}
