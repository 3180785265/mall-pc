package com.itheima.mall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.mall.dao.MallAdminMapper;
import com.itheima.mall.domain.MallAdmin;
import com.itheima.mall.service.MallAdminService;
import org.springframework.stereotype.Service;

@Service
public class MallAdminServiceImpl  extends ServiceImpl<MallAdminMapper, MallAdmin> implements MallAdminService {
}
