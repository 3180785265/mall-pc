package com.itheima.mall.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.mall.common.R;
import com.itheima.mall.domain.CmsSubject;
import com.itheima.mall.service.ICmsSubjectService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 专题表 前端控制器
 * </p>
 *
 * @author 小刘
 * @since 2022-11-20
 */
@RestController
@RequestMapping("/subject")
public class CmsSubjectController {

    @Autowired
    private ICmsSubjectService iCmsSubjectService;

    @GetMapping("/list")
    public R list(CmsSubject cmsSubject,
                  @RequestParam(value = "pageSize", required = false) Integer pageSize,
                  @RequestParam(value = "pageNum", required = false) Integer pageNum) {
        LambdaQueryWrapper<CmsSubject> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(cmsSubject.getTitle()), CmsSubject::getTitle, cmsSubject.getTitle());
        wrapper.eq(cmsSubject.getRecommendStatus() != null, CmsSubject::getRecommendStatus, cmsSubject.getRecommendStatus());
        wrapper.orderByAsc(CmsSubject::getId);

        if (pageNum == null || pageSize == null) {
            return R.success(iCmsSubjectService.list(wrapper));
        }
        Page<CmsSubject> smsHomeAdvertisePage = new Page<>(pageNum, pageSize);
        Page<CmsSubject> page = iCmsSubjectService.page(smsHomeAdvertisePage, wrapper);
        return R.success(page);
    }
}

