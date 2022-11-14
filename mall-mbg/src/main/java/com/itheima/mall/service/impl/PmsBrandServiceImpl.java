package com.itheima.mall.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.mall.dao.PmsBrandDao;
import com.itheima.mall.domain.PmsBrand;
import com.itheima.mall.service.IPmsBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 品牌表 服务实现类
 * </p>
 *
 * @author 小刘
 * @since 2022-11-14
 */
@Service
public class PmsBrandServiceImpl extends ServiceImpl<PmsBrandDao, PmsBrand> implements IPmsBrandService {
    @Autowired
    private PmsBrandDao pmsBrandDao;
    @Override
    public IPage<PmsBrand> recommendBrand(Integer pageNum, Integer pageSize) {
        Page<PmsBrand> pageInfo = new Page<>(pageNum, pageSize);
        IPage<PmsBrand> iPage = pmsBrandDao.recommendBrand(pageInfo);
        return iPage;
    }
}
