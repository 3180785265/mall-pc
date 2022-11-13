package com.ithiema.mall.domain;

import com.itheima.mall.domain.PmsBrand;
import com.itheima.mall.domain.SmsHomeAdvertise;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class HomeContentResult implements Serializable {

    private List<SmsHomeAdvertise> SmsHomeAdvertiseList;
    private List<PmsBrand> pmsBrandList;
}
