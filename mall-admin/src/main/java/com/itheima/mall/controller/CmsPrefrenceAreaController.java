package com.itheima.mall.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.mall.common.R;
import com.itheima.mall.domain.CmsPrefrenceArea;
import com.itheima.mall.service.ICmsPrefrenceAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 优选专区 前端控制器
 * </p>
 *
 * @author 小刘
 * @since 2022-11-20
 */
@RestController
@RequestMapping("/prefrenceArea")
public class CmsPrefrenceAreaController {
    @Autowired
    private ICmsPrefrenceAreaService iCmsPrefrenceAreaService;

    @GetMapping("/list")
    public R list(CmsPrefrenceArea cmsPrefrenceArea,
                  @RequestParam(value = "pageSize", required = false) Integer pageSize,
                  @RequestParam(value = "pageNum", required = false) Integer pageNum) {
        LambdaQueryWrapper<CmsPrefrenceArea> wrapper = new LambdaQueryWrapper<>();

        wrapper.orderByAsc(CmsPrefrenceArea::getId);

        if (pageNum == null || pageSize == null) {
            return R.success(iCmsPrefrenceAreaService.list(wrapper));
        }
        Page<CmsPrefrenceArea> smsHomeAdvertisePage = new Page<>(pageNum, pageSize);
        Page<CmsPrefrenceArea> page = iCmsPrefrenceAreaService.page(smsHomeAdvertisePage, wrapper);
        return R.success(page);
    }
}

