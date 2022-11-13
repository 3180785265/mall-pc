package com.itheima.mall.service.impl;

import com.itheima.mall.domain.PmsBrand;
import com.itheima.mall.dao.PmsBrandDao;
import com.itheima.mall.service.IPmsBrandService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
    public List<PmsBrand> recommendBrand(int pageNum, int pageSize) {
        return pmsBrandDao.recommendBrand(pageNum,pageSize);
    }
}
