package com.itheima.mall.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.mall.common.R;
import com.itheima.mall.domain.PmsBrand;
import com.itheima.mall.service.IPmsBrandService;
import com.itheima.mall.service.PortalBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * 首页品牌推荐管理Controller
 * Created by macro on 2020/5/15.
 */
@Controller
@RequestMapping("/brand")
public class PortalBrandController {

    @Autowired
    private PortalBrandService homeBrandService;
    @Autowired
    private IPmsBrandService iPmsBrandService;

    @RequestMapping(value = "/recommendList", method = RequestMethod.GET)
    @ResponseBody
    public R recommendList(@RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize,
                                           @RequestParam(value = "page", defaultValue = "1") Integer page) {

        IPage<PmsBrand> pmsBrandPage = homeBrandService.recommendList(page, pageSize);
        return R.success(pmsBrandPage);
    }

    @RequestMapping(value = "/detail/{brandId}", method = RequestMethod.GET)
    @ResponseBody
    public R detail(@PathVariable("brandId") Long brandId) {

        PmsBrand byId = iPmsBrandService.getById(brandId);
        return R.success(byId);
    }

}
