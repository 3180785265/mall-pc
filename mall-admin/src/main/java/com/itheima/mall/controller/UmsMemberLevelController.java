package com.itheima.mall.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.mall.common.R;
import com.itheima.mall.domain.UmsMemberLevel;
import com.itheima.mall.service.IUmsMemberLevelService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 会员等级表 前端控制器
 * </p>
 *
 * @author 小刘
 * @since 2022-11-21
 */
@RestController
@RequestMapping("/memberLevel")
public class UmsMemberLevelController {

    @Autowired
    private IUmsMemberLevelService iUmsMemberLevelService;

    @GetMapping("/list")
    public R list(UmsMemberLevel umsMemberLevel,
                  @RequestParam(value = "pageSize", required = false) Integer pageSize,
                  @RequestParam(value = "pageNum", required = false) Integer pageNum) {
        if (pageNum == null || pageSize == null) {
            return R.success(iUmsMemberLevelService.list());
        }
        Page<UmsMemberLevel> smsHomeAdvertisePage = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UmsMemberLevel> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(umsMemberLevel.getName()), UmsMemberLevel::getName, umsMemberLevel.getName());

        wrapper.orderByAsc(UmsMemberLevel::getId);
        Page<UmsMemberLevel> page = iUmsMemberLevelService.page(smsHomeAdvertisePage, wrapper);

        return R.success(page);
    }
}

