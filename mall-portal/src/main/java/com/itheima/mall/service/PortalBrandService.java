package com.itheima.mall.service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.mall.domain.PmsBrand;


public interface PortalBrandService {
    IPage<PmsBrand> recommendList(Integer pageNum, Integer pageSize);

}
