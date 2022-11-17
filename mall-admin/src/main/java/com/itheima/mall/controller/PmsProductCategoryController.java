package com.itheima.mall.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.mall.common.R;
import com.itheima.mall.domain.PmsBrand;
import com.itheima.mall.domain.PmsProduct;
import com.itheima.mall.domain.PmsProductCategory;
import com.itheima.mall.domain.PmsProductCategoryAttributeRelation;
import com.itheima.mall.dto.PmsBrandParam;
import com.itheima.mall.dto.PmsProductCategoryParam;
import com.itheima.mall.dto.PmsProductCategoryQueryParam;
import com.itheima.mall.dto.PmsProductQueryParam;
import com.itheima.mall.service.IPmsProductCategoryAttributeRelationService;
import com.itheima.mall.service.IPmsProductCategoryService;
import com.itheima.mall.service.IPmsProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 产品分类 前端控制器
 * </p>
 *
 * @author 小刘
 * @since 2022-11-16
 */
@RestController
@RequestMapping("/productCategory")
public class PmsProductCategoryController {

    @Autowired
    private IPmsProductCategoryService iPmsProductCategoryService;
    @Autowired
    private IPmsProductCategoryAttributeRelationService iPmsProductCategoryAttributeRelationService;

    @GetMapping("/list")
    public R list(PmsProductCategoryQueryParam pmsProductQueryParam,
                  @RequestParam(value = "pageSize", required = false) Integer pageSize,
                  @RequestParam(value = "pageNum", required = false) Integer pageNum) {
        if (pageNum == null || pageSize == null) {
            return R.success(iPmsProductCategoryService.list());
        }
        Page<PmsProductCategory> smsHomeAdvertisePage = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PmsProductCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(pmsProductQueryParam.getName()), PmsProductCategory::getName, pmsProductQueryParam.getName());
        wrapper.orderByAsc(PmsProductCategory::getId);
        Page<PmsProductCategory> page = iPmsProductCategoryService.page(smsHomeAdvertisePage, wrapper);

        return R.success(page);
    }

    /**
     * 添加商品分类
     *
     * @param pmsProductCategoryParam
     * @return
     */
    @PostMapping("/create")
    public R create(@Validated @RequestBody PmsProductCategoryParam pmsProductCategoryParam) {

        PmsProductCategory pmsProductCategory = new PmsProductCategory();
        BeanUtils.copyProperties(pmsProductCategoryParam, pmsProductCategory);

        iPmsProductCategoryService.save(pmsProductCategory);

        List<Long> ids = pmsProductCategoryParam.getIds();

        if (CollectionUtils.isNotEmpty(ids)) {

            List<PmsProductCategoryAttributeRelation> pmsProductCategoryAttributeRelations=ids.stream().map(id->{
                PmsProductCategoryAttributeRelation pmsProductCategoryAttributeRelation = new PmsProductCategoryAttributeRelation();
                pmsProductCategoryAttributeRelation.setProductCategoryId(pmsProductCategory.getId());
                pmsProductCategoryAttributeRelation.setProductAttributeId(id);
                return pmsProductCategoryAttributeRelation;
            }).collect(Collectors.toList());

            iPmsProductCategoryAttributeRelationService.saveBatch(pmsProductCategoryAttributeRelations);
        }
        return R.success("新增成功");
    }


    /**
     * 修改商品分类
     *
     * @param
     * @return
     */
    @PutMapping("/update/{id}")
    public R update(@PathVariable("id")Long id,@Validated @RequestBody PmsProductCategoryParam pmsProductCategoryParam){

        PmsProductCategory pmsProductCategory = new PmsProductCategory();
        BeanUtils.copyProperties(pmsProductCategoryParam, pmsProductCategory);
        pmsProductCategoryParam.setId(id);

        iPmsProductCategoryService.updateById(pmsProductCategory);

        LambdaUpdateWrapper<PmsProductCategoryAttributeRelation> pmsProductCategoryAttributeRelationLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        pmsProductCategoryAttributeRelationLambdaUpdateWrapper.eq(PmsProductCategoryAttributeRelation::getProductCategoryId,id);
        iPmsProductCategoryAttributeRelationService.remove(pmsProductCategoryAttributeRelationLambdaUpdateWrapper);
        List<Long> ids = pmsProductCategoryParam.getIds();
        if (CollectionUtils.isNotEmpty(ids)) {

            List<PmsProductCategoryAttributeRelation> pmsProductCategoryAttributeRelations=ids.stream().map(item->{
                PmsProductCategoryAttributeRelation pmsProductCategoryAttributeRelation = new PmsProductCategoryAttributeRelation();
                pmsProductCategoryAttributeRelation.setProductCategoryId(pmsProductCategory.getId());
                pmsProductCategoryAttributeRelation.setProductAttributeId(item);
                return pmsProductCategoryAttributeRelation;
            }).collect(Collectors.toList());

            iPmsProductCategoryAttributeRelationService.saveBatch(pmsProductCategoryAttributeRelations);
        }        return R.success("修改成功");
    }

    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable("id")Long id){

        iPmsProductCategoryService.removeById(id);
        LambdaUpdateWrapper<PmsProductCategoryAttributeRelation> pmsProductCategoryAttributeRelationLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        pmsProductCategoryAttributeRelationLambdaUpdateWrapper.eq(PmsProductCategoryAttributeRelation::getProductCategoryId,id);
        iPmsProductCategoryAttributeRelationService.remove(pmsProductCategoryAttributeRelationLambdaUpdateWrapper);
        return R.success("删除成功");
    }

    @GetMapping("/{id}")
    public R getItem(@PathVariable("id")Long id){

        PmsProductCategory productCategory = iPmsProductCategoryService.getById(id);

        LambdaQueryWrapper<PmsProductCategoryAttributeRelation> pmsProductCategoryAttributeRelationLambdaQueryWrapper = new LambdaQueryWrapper<>();
        pmsProductCategoryAttributeRelationLambdaQueryWrapper.eq(PmsProductCategoryAttributeRelation::getProductCategoryId,id);
        return R.success(productCategory);
    }
}

