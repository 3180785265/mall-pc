package com.itheima.mall.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.mall.common.R;
import com.itheima.mall.domain.PmsProductAttributeCategory;
import com.itheima.mall.dto.PmsProductAttributeCategoryParam;
import com.itheima.mall.service.IPmsProductAttributeCategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 产品属性分类表 前端控制器
 * </p>
 *
 * @author 小刘
 * @since 2022-11-18
 */
@RestController
@RequestMapping("/productAttribute/category")
public class PmsProductAttributeCategoryController {

    @Autowired
    private IPmsProductAttributeCategoryService iPmsProductAttributeCategoryService;

    @GetMapping("/list")
    public R list(PmsProductAttributeCategoryParam pmsProductAttributeCategoryParam,
                  @RequestParam(value = "pageSize", required = false) Integer pageSize,
                  @RequestParam(value = "pageNum",  required = false) Integer pageNum){
        if(pageNum==null||pageSize==null){
            return R.success( iPmsProductAttributeCategoryService.list());
        }
        Page<PmsProductAttributeCategory> smsHomeAdvertisePage = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<PmsProductAttributeCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(pmsProductAttributeCategoryParam.getName()), PmsProductAttributeCategory::getName,pmsProductAttributeCategoryParam.getName());

        wrapper.orderByAsc(PmsProductAttributeCategory::getId);


        Page<PmsProductAttributeCategory> page = iPmsProductAttributeCategoryService.page(smsHomeAdvertisePage, wrapper);
        return R.success(page);
    }


    @PostMapping("/create")
    public R create(@Validated @RequestBody PmsProductAttributeCategory pmsProductAttributeCategory){

        iPmsProductAttributeCategoryService.save(pmsProductAttributeCategory);
        return R.success("新增成功");
    }



    @PutMapping("/update/{id}")
    public R update(@PathVariable("id")Long id,@Validated @RequestBody PmsProductAttributeCategory pmsProductAttributeCategory){


        pmsProductAttributeCategory.setId(id);
        iPmsProductAttributeCategoryService.updateById(pmsProductAttributeCategory);
        return R.success("修改成功");
    }

    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable("id")Long id){

        iPmsProductAttributeCategoryService.removeById(id);
        return R.success("删除成功");
    }
}

