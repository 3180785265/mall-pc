package com.itheima.mall.dto;

import com.itheima.mall.domain.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PmsProductParam extends PmsProduct implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 会员价格
     */

    private List<PmsMemberPrice> memberPriceList;

    /**
     * 商品自定义属性值
     */
    private List<PmsProductAttributeValue> productAttributeValueList;

    @ApiModelProperty("商品阶梯价格设置")
    private List<PmsProductLadder> productLadderList;
    @ApiModelProperty("商品满减价格设置")
    private List<PmsProductFullReduction> productFullReductionList;
    @ApiModelProperty("商品的sku库存信息")
    private List<PmsSkuStock> skuStockList;
    @ApiModelProperty("专题和商品关系")
    private List<CmsSubjectProductRelation> subjectProductRelationList;
    @ApiModelProperty("优选专区和商品的关系")
    private List<CmsPrefrenceAreaProductRelation> prefrenceAreaProductRelationList;


}
