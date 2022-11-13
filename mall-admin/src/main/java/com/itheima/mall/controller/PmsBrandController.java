package com.itheima.mall.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.mall.common.R;
import com.itheima.mall.domain.PmsBrand;
import com.itheima.mall.service.IPmsBrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 品牌表 前端控制器
 * </p>
 *
 * @author 小刘
 * @since 2022-11-14
 */
@RestController
@RequestMapping("/pmsBrand")
public class PmsBrandController {
    @Autowired
    private IPmsBrandService iPmsBrandService;

    @GetMapping("/list")
    public R list(@RequestParam(value = "keyword", required = false) String  keyword ,
                  @RequestParam(value = "recommendStatus", required = false) Integer recommendStatus,
                  @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                  @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum){

        Page<PmsBrand> smsHomeAdvertisePage = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<PmsBrand> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(keyword),PmsBrand::getName,keyword);
        wrapper.orderByDesc(PmsBrand::getId);
        Page<PmsBrand> page = iPmsBrandService.page(smsHomeAdvertisePage, wrapper);

        return R.success(page);
    }
}

