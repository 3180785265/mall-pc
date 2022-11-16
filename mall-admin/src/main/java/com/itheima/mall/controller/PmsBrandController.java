package com.itheima.mall.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.mall.common.R;
import com.itheima.mall.domain.PmsBrand;
import com.itheima.mall.dto.PmsBrandParam;
import com.itheima.mall.service.IPmsBrandService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 品牌表 前端控制器
 * </p>
 *
 * @author 小刘
 * @since 2022-11-14
 */
@RestController
@RequestMapping("/brand")
@Slf4j
public class PmsBrandController {
    @Autowired
    private IPmsBrandService iPmsBrandService;

    @GetMapping("/list")
    public R list(@RequestParam(value = "keyword", required = false) String  keyword ,
                  @RequestParam(value = "pageSize", required = false) Integer pageSize,
                  @RequestParam(value = "pageNum",  required = false) Integer pageNum){
        if(pageNum==null||pageSize==null){
            return R.success( iPmsBrandService.list());
        }
        Page<PmsBrand> smsHomeAdvertisePage = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<PmsBrand> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(keyword),PmsBrand::getName,keyword);
        wrapper.orderByDesc(PmsBrand::getId);
        Page<PmsBrand> page = iPmsBrandService.page(smsHomeAdvertisePage, wrapper);

        return R.success(page);
    }

    @PostMapping("/create")
    public R create(@Validated @RequestBody PmsBrandParam pmsBrandParam){

        PmsBrand pmsBrand = new PmsBrand();
        BeanUtils.copyProperties(pmsBrandParam, pmsBrand);
        iPmsBrandService.save(pmsBrand);
        return R.success("新增成功");
    }



    @PutMapping("/update/{id}")
    public R update(@PathVariable("id")Long id,@Validated @RequestBody PmsBrandParam pmsBrandParam){

        PmsBrand pmsBrand = new PmsBrand();
        BeanUtils.copyProperties(pmsBrandParam, pmsBrand);
        pmsBrand.setId(id);
        iPmsBrandService.save(pmsBrand);
        return R.success("修改成功");
    }

    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable("id")Long id){

        iPmsBrandService.removeById(id);
        return R.success("删除成功");
    }

    @GetMapping("/{id}")
    public R getItem(@PathVariable("id")Long id){

        PmsBrand byId = iPmsBrandService.getById(id);
        return R.success(byId);
    }

    /**
     * 批量更新显示状态
     * @param
     * @return
     */
    @PostMapping("/updateBach")
    public R updateStatus(@RequestParam(value = "ids",required = false) List<Long> ids , @RequestParam("showStatus")Integer showStatus){
        log.info("ids-{}",ids);
        log.info("showStatus-{}",showStatus);
//        if(CollectionUtils.isNotEmpty(ids)){
//            return R.error("请选择数据");
//        }

        List<PmsBrand> pmsBrands = ids.stream().map(item -> {
            PmsBrand pmsBrand = new PmsBrand();
            pmsBrand.setId(item);
            pmsBrand.setShowStatus(showStatus);
            return pmsBrand;
        }).collect(Collectors.toList());
         iPmsBrandService.saveOrUpdateBatch(pmsBrands);
        return R.success("批量修改从成功");
    }
}

