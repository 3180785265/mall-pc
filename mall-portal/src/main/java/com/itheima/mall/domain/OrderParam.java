package com.itheima.mall.domain;

import lombok.Data;

import java.util.List;

@Data
public class OrderParam {
    private Long couponId; //优惠卷id
    private List<Long> carIds;// 用户选中的购物车
    private Long memberReceiveAddressId;//收获地址id
    private Integer pyType;//用户的支付方式
}
