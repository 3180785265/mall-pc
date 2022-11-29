package com.itheima.mall.controller;

import com.itheima.mall.common.R;
import com.itheima.mall.domain.PmsPortalProductDetail;
import com.itheima.mall.service.PmsPortalProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class PmsPortalProductController {
    @Autowired
    private PmsPortalProductService portalProductService;

    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    @ResponseBody
    public R<PmsPortalProductDetail> detail(@PathVariable Long id) {
        PmsPortalProductDetail productDetail = portalProductService.detail(id);
        return R.success(productDetail);
    }
}
