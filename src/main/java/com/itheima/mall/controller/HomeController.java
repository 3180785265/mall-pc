package com.itheima.mall.controller;
import com.itheima.mall.common.R;
import com.itheima.mall.domain.HomeContentResult;
import com.itheima.mall.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/home")
public class HomeController {


    @Autowired
    private HomeService homeService;

    /**
     * 获取首页数据
     * @return
     */
    @RequestMapping("/content")
    public R content (){
        HomeContentResult content = homeService.content();

        return R.success(content);
    }
}
