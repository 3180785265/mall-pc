package com.itheima.mall.intercepter;

import com.itheima.mall.common.BaseContext;
import com.itheima.mall.common.exception.BusException;
import com.itheima.mall.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SysInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){

            //        判断是否携带了token
            if (StringUtils.isBlank(request.getHeader("Authorization"))) {
                throw new BusException("token不存在！");
            }
//        判断token的有效性
            String token = request.getHeader("Authorization").split(" ")[1];
            Claims claims = JwtUtils.validateJWT(token).getClaims();

            if (claims == null) {
                throw new BusException("鉴权失败！");
            }


//        使用ThreadLocal,将用户的id存储到ThreadLocal当中！
            BaseContext.setCurrentData(claims.getId());


//        对请求放行

            return true;
        }else {
            return false;
        }



    }
}


