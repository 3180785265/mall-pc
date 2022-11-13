package com.itheima.mall.service;

import com.itheima.mall.domain.PmsBrand;
import com.baomidou.mybatisplus.extension.service.IService;

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

    List<PmsBrand> recommendBrand(int pageNum, int pageSize);
}
