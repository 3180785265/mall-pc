package com.itheima.mall.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.mall.common.R;
import com.itheima.mall.domain.PmsProductAttribute;
import com.itheima.mall.dto.PmsProductAttributeParam;
import com.itheima.mall.dto.PmsProductAttributeQueryParam;
import com.itheima.mall.service.IPmsProductAttributeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 商品属性参数表 前端控制器
 * </p>
 *
 * @author 小刘
 * @since 2022-11-18
 */
@RestController
@RequestMapping("/productAttribute")
public class PmsProductAttributeController {
    @Autowired
    private IPmsProductAttributeService iPmsProductAttributeService;

    @GetMapping("/list")
    public R list(PmsProductAttributeQueryParam pmsProductAttributeParam,
                  @RequestParam(value = "pageSize", required = false) Integer pageSize,
                  @RequestParam(value = "pageNum",  required = false) Integer pageNum){
        LambdaQueryWrapper<PmsProductAttribute> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(pmsProductAttributeParam.getName()), PmsProductAttribute::getName,pmsProductAttributeParam.getName());
        wrapper.eq(pmsProductAttributeParam.getProductAttributeCategoryId() != null, PmsProductAttribute::getProductAttributeCategoryId, pmsProductAttributeParam.getProductAttributeCategoryId());
        wrapper.eq(pmsProductAttributeParam.getType() != null, PmsProductAttribute::getType, pmsProductAttributeParam.getType());
        wrapper.orderByAsc(PmsProductAttribute::getId);

        if(pageNum==null||pageSize==null){
            return R.success( iPmsProductAttributeService.list(wrapper));
        }
        Page<PmsProductAttribute> smsHomeAdvertisePage = new Page<>(pageNum,pageSize);
        Page<PmsProductAttribute> page = iPmsProductAttributeService.page(smsHomeAdvertisePage, wrapper);
        return R.success(page);
    }


    @PostMapping("/create")
    public R create(@Validated @RequestBody PmsProductAttributeParam pmsProductAttributeParam){

        PmsProductAttribute pmsProductAttribute = new PmsProductAttribute();
        BeanUtils.copyProperties(pmsProductAttributeParam,pmsProductAttribute);
        iPmsProductAttributeService.save(pmsProductAttribute);


        return R.success("新增成功");
    }



    @PutMapping("/update/{id}")
    public R update(@PathVariable("id")Long id,@Validated @RequestBody PmsProductAttributeParam pmsProductAttributeParam){



        PmsProductAttribute pmsProductAttribute = new PmsProductAttribute();
        BeanUtils.copyProperties(pmsProductAttributeParam,pmsProductAttribute);
        pmsProductAttribute.setId(id);
        iPmsProductAttributeService.save(pmsProductAttribute);
        return R.success("修改成功");
    }

    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable("id")Long id){

        iPmsProductAttributeService.removeById(id);
        return R.success("删除成功");
    }
}

