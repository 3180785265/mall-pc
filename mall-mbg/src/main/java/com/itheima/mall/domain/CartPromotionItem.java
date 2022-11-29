package com.itheima.mall.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartPromotionItem extends OmsCartItem {
    //    @ApiModelProperty("促销活动信息")
    private String promotionMessage;
    //    @ApiModelProperty("促销活动减去的金额，针对每个商品")
    private BigDecimal reduceAmount;
    //    @ApiModelProperty("剩余库存-锁定库存")
    private Integer realStock;//(可能是商品剩余的真实库存)
    //    @ApiModelProperty("购买商品赠送积分")
    private Integer integration;
    //    @ApiModelProperty("购买商品赠送成长值")
    private Integer growth;

    //优惠价格
    private BigDecimal couponPrice;
}
