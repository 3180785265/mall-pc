package com.itheima.mall.service.impl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.mall.domain.PmsBrand;
import com.itheima.mall.service.IPmsBrandService;
import com.itheima.mall.service.PortalBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PortalBrandServiceImpl implements PortalBrandService {
    @Autowired
    private IPmsBrandService iPmsBrandService;
    @Override
    public  IPage<PmsBrand>  recommendList(Integer pageNum, Integer pageSize) {
        IPage<PmsBrand> pmsBrandIPage = iPmsBrandService.recommendBrand(pageNum, pageSize);
        return pmsBrandIPage;
    }
}
