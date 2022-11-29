package com.itheima.mall.domain;

import lombok.Data;

import java.util.List;

@Data
public class SmsCouponHistoryDetail extends SmsCouponHistory {
    //    @ApiModelProperty("相关优惠券信息")
    private SmsCoupon coupon;
    //    @ApiModelProperty("优惠券关联商品")
    private List<SmsCouponProductRelation> productRelationList;
    //    @ApiModelProperty("优惠券关联商品分类")
    private List<SmsCouponProductCategoryRelation> categoryRelationList;

}
