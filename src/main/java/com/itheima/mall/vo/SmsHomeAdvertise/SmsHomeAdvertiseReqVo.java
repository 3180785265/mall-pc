package com.itheima.mall.vo.SmsHomeAdvertise;

import com.itheima.mall.domain.SmsHomeAdvertise;
import lombok.Data;

import java.io.Serializable;
@Data
public class SmsHomeAdvertiseReqVo extends SmsHomeAdvertise implements Serializable {
   private Integer pageNum;
   private Integer pageSize;
   private String  starTimeStr;
   private String  endTimeStr;
}
