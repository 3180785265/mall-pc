package com.itheima.mall.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class HomeContentResult implements Serializable {

    private List<SmsHomeAdvertise> SmsHomeAdvertiseList;
    private List<PmsBrand> pmsBrandList;
}
