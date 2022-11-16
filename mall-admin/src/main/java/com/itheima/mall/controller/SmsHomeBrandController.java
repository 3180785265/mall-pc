package com.itheima.mall.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.mall.common.R;
import com.itheima.mall.domain.SmsHomeBrand;
import com.itheima.mall.service.ISmsHomeBrandService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 首页推荐品牌表 前端控制器
 * </p>
 *
 * @author 小刘
 * @since 2022-11-14
 */
@RestController
@RequestMapping("/home/brand")
@Slf4j
public class SmsHomeBrandController {
    @Autowired
    private ISmsHomeBrandService iSmsHomeBrandService;



    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public R list(@RequestParam(value = "brandName", required = false) String brandName,
                                 @RequestParam(value = "recommendStatus", required = false) Integer recommendStatus,
                                 @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                 @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        Page<SmsHomeBrand> smsHomeAdvertisePage = new Page<>(pageNum,pageSize);

        LambdaQueryWrapper<SmsHomeBrand> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNoneEmpty(brandName),SmsHomeBrand::getBrandName,brandName);
        wrapper.eq(recommendStatus!=null,SmsHomeBrand::getRecommendStatus,recommendStatus);
        wrapper.orderByDesc(SmsHomeBrand::getId);
        Page<SmsHomeBrand> page = iSmsHomeBrandService.page(smsHomeAdvertisePage, wrapper);
        return R.success(page);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public R create(@RequestBody List<SmsHomeBrand>smsHomeBrands) {
        log.info("smsHomeBrands-{}",smsHomeBrands);

        if(CollectionUtils.isNotEmpty(smsHomeBrands)){
            iSmsHomeBrandService.saveBatch(smsHomeBrands);
        }
        return R.success("保存成功");
    }
}

