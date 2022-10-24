package com.itheima.mall.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.mall.common.BaseContext;
import com.itheima.mall.common.R;
import com.itheima.mall.constant.SystemConstant;
import com.itheima.mall.domain.MallAdmin;
import com.itheima.mall.service.MallAdminService;
import com.itheima.mall.util.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mall/admin")
public class MallAdminController {

    @Autowired
    private MallAdminService mallAdminService;


    /**
     * 管理员登录
     *
     * @param mallAdmin
     * @return
     */
    @PostMapping("/login")
    public R login(@RequestBody MallAdmin mallAdmin) {
//        判断用户名密码是否为空

        if (StringUtils.isBlank(mallAdmin.getMobile())) {
            return R.error("请填写手机号");
        }
        if (StringUtils.isBlank(mallAdmin.getPassword())) {
            return R.error("请填写密码");
        }


//        根据用户名查询数据库数据
        LambdaQueryWrapper<MallAdmin> mallAdminLambdaQueryWrapper = new LambdaQueryWrapper<>();
        mallAdminLambdaQueryWrapper.eq(MallAdmin::getMobile, mallAdmin.getMobile());
        MallAdmin admin = mallAdminService.getOne(mallAdminLambdaQueryWrapper);
        if (admin == null) {
            return R.error("用户不存在");
        }
        //        判断查询的用户数据是否为空，
        if (admin == null) {
            return R.error("用户不存在");
        }

//        否则判断用户的密码是否正确，
        if (!admin.getPassword().equals(mallAdmin.getPassword())) {
            return R.error("用户名密码错误");
        }


//        正确则创建jwt返回，
        String token = JwtUtils.createJWT(admin.getId().toString(), admin.getUsername(), SystemConstant.JWT_TTL);

        return R.success(token);


    }


    /**
     * 退出登录
     *
     * @return
     */
    @PostMapping("/logout")
    public R logout() {

        return R.success("退出成功！");
    }




    /**
     * 用户修改信息
     */


    /**
     * 获取用户信息接口
     */

    @GetMapping("/userInfo")
    public R test() {
        int userId = Integer.parseInt(BaseContext.getCurrentData().toString());
        LambdaQueryWrapper<MallAdmin> mallAdminLambdaQueryWrapper = new LambdaQueryWrapper<>();
        mallAdminLambdaQueryWrapper.eq(MallAdmin::getId,userId);
        MallAdmin mallAdmin = mallAdminService.getOne(mallAdminLambdaQueryWrapper);
        if(mallAdmin==null){
            return R.error("用户不存在");
        }
        return R.success(mallAdmin);
    }





}
