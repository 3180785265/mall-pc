package com.itheima.mall.common;

import com.itheima.mall.common.exception.BusException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;

//全局异常处理类
//@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        log.error(ex.getMessage());
        if (ex.getMessage().contains("Duplicate entry")) {
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }

    //业务异常处理
    @ExceptionHandler(BusException.class)
    public R<String> BusExceptionHandler(BusException ex) {
        log.error(ex.getMessage());
        return R.error(ex.getMessage());
    }

    //
    @ExceptionHandler(Exception.class)
    public R<String> Exception(Exception ex) {
        log.error("异常-{}",ex.getMessage());
        return R.error(ex.getMessage());
    }


}
//4