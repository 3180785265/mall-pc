package com.itheima.mall.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.mall.common.R;
import com.itheima.mall.domain.SmsHomeAdvertise;
import com.itheima.mall.service.ISmsHomeAdvertiseService;
import com.itheima.mall.vo.SmsHomeAdvertise.SmsHomeAdvertiseReqVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 小刘
 * @since 2022-10-23
 */
@RestController
@RequestMapping("/home/advertise/")
public class SmsHomeAdvertiseController {

    @Autowired
    private ISmsHomeAdvertiseService iSmsHomeAdvertiseService;

    @GetMapping("/list")
    public R  list(SmsHomeAdvertiseReqVo queryForm){

        Page<SmsHomeAdvertise> smsHomeAdvertisePage = new Page<>(queryForm.getPageNum(),queryForm.getPageSize());
        LambdaQueryWrapper<SmsHomeAdvertise> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNoneEmpty(queryForm.getName()),SmsHomeAdvertise::getName,queryForm.getName());
        wrapper.eq(queryForm.getType()!=null,SmsHomeAdvertise::getType,queryForm.getType());
        wrapper.between(StringUtils.isNoneEmpty(queryForm.getStarTimeStr()),SmsHomeAdvertise::getStartTime,queryForm.getStarTimeStr()+" 00:00:00",queryForm.getStarTimeStr()+" 23:59:59");
        wrapper.between(StringUtils.isNoneEmpty(queryForm.getEndTimeStr()),SmsHomeAdvertise::getEndTime,queryForm.getEndTimeStr()+" 00:00:00",queryForm.getEndTimeStr()+" 23:59:59");
        wrapper.orderByDesc(SmsHomeAdvertise::getId);
        Page<SmsHomeAdvertise> page = iSmsHomeAdvertiseService.page(smsHomeAdvertisePage, wrapper);

        return R.success(page);
    }

    @DeleteMapping("/delete")
    public R  delete(@RequestBody  ArrayList<Integer>ids){

        if(CollectionUtils.isNotEmpty(ids)){
            iSmsHomeAdvertiseService.removeByIds(ids);
        }

        return R.success("删除成功");
    }

    /**
     * 添加数据
     * @param smsHomeAdvertise
     * @return
     */
    @PostMapping("/save")
    public R  save(@RequestBody  SmsHomeAdvertise smsHomeAdvertise){
//        判断字段是否为null

        iSmsHomeAdvertiseService.save(smsHomeAdvertise);

        return R.success("添加成功");
    }

    /**
     * 编辑
     * @param
     * @return
     */
    @PutMapping("/{id}")
    public R  update(@PathVariable Long id,@RequestBody SmsHomeAdvertise smsHomeAdvertise){
//        判断字段是否为null
        if(id==null){
            return R.success("src/test");
        }
        smsHomeAdvertise.setId(id);
        iSmsHomeAdvertiseService.updateById(smsHomeAdvertise);

        return R.success("保存成功");
    }

    /**
     * 详情
     * @param
     * @return
     */
    @GetMapping("/{id}")
    public R  getInfo(@PathVariable Integer id){
//        判断字段是否为null
        if(id==null){
            return R.success("src/test");
        }
        SmsHomeAdvertise smsHomeAdvertise = iSmsHomeAdvertiseService.getById(id);

        return R.success(smsHomeAdvertise);
    }
}

