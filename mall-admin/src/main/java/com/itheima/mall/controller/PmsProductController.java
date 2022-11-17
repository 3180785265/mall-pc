package com.itheima.mall.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.mall.common.R;
import com.itheima.mall.domain.PmsProduct;
import com.itheima.mall.dto.PmsProductQueryParam;
import com.itheima.mall.service.IPmsProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 商品信息 前端控制器
 * </p>
 *
 * @author 小刘
 * @since 2022-11-16
 */
@RestController
@RequestMapping("/product")
@Slf4j
public class PmsProductController {
    
    @Autowired
    private IPmsProductService iPmsProductService;

    @GetMapping("/list")
    public R list(PmsProductQueryParam pmsProductQueryParam,
                  @RequestParam(value = "pageSize", required = false) Integer pageSize,
                  @RequestParam(value = "pageNum",  required = false) Integer pageNum){
        if(pageNum==null||pageSize==null){
            return R.success( iPmsProductService.list());
        }
        Page<PmsProduct> smsHomeAdvertisePage = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<PmsProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(pmsProductQueryParam.getKeyword()),PmsProduct::getName,pmsProductQueryParam.getKeyword());
        wrapper.like(StringUtils.isNotEmpty(pmsProductQueryParam.getProductSn()),PmsProduct::getProductSn,pmsProductQueryParam.getProductSn());
        wrapper.eq((pmsProductQueryParam.getProductCategoryId()!=null),PmsProduct::getProductCategoryId,pmsProductQueryParam.getProductCategoryId());
        wrapper.eq((pmsProductQueryParam.getBrandId()!=null),PmsProduct::getBrandId,pmsProductQueryParam.getBrandId());
        wrapper.eq((pmsProductQueryParam.getPublishStatus()!=null),PmsProduct::getPublishStatus,pmsProductQueryParam.getPublishStatus());
        wrapper.eq((pmsProductQueryParam.getVerifyStatus()!=null),PmsProduct::getVerifyStatus,pmsProductQueryParam.getVerifyStatus());
        wrapper.orderByAsc(PmsProduct::getId);
        Page<PmsProduct> page = iPmsProductService.page(smsHomeAdvertisePage, wrapper);

        return R.success(page);
    }








}

