package com.itheima.mall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.mall.domain.PmsBrand;

import java.util.List;

/**
 * <p>
 * 品牌表 服务类
 * </p>
 *
 * @author 小刘
 * @since 2022-11-14
 */
public interface IPmsBrandService extends IService<PmsBrand> {

    IPage<PmsBrand> recommendBrand(Integer pageNum, Integer pageSize);
}
