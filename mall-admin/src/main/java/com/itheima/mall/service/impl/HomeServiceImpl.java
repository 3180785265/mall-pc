package com.itheima.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.mall.domain.HomeContentResult;
import com.itheima.mall.domain.PmsBrand;
import com.itheima.mall.domain.SmsHomeAdvertise;
import com.itheima.mall.service.HomeService;
import com.itheima.mall.service.IPmsBrandService;
import com.itheima.mall.service.ISmsHomeAdvertiseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeServiceImpl implements HomeService {
    @Autowired
    private ISmsHomeAdvertiseService iSmsHomeAdvertiseService;

    @Autowired
    private IPmsBrandService iPmsBrandService;
    @Override
    public HomeContentResult content() {

        //获取首页广告
        LambdaQueryWrapper<SmsHomeAdvertise> w = new LambdaQueryWrapper<>();
        w.eq(SmsHomeAdvertise::getStatus,1);
        w.orderByAsc(SmsHomeAdvertise::getId);
        List<SmsHomeAdvertise> smsHomeAdvertiseList = iSmsHomeAdvertiseService.list(w);
        HomeContentResult homeContentResult = new HomeContentResult();

        //获取首页品牌
        List<PmsBrand> pmsBrandList=iPmsBrandService.recommendBrand( 1, 4);
//        List<PmsBrand> pmsBrandList = iPmsBrandService.list();

        homeContentResult.setSmsHomeAdvertiseList(smsHomeAdvertiseList);
        homeContentResult.setPmsBrandList(pmsBrandList);

        return homeContentResult;
    }
}
