package com.itheima.mall.dao;

import com.itheima.mall.domain.PmsBrand;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

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

    List<PmsBrand> recommendBrand(int pageNum, int pageSize);
}
