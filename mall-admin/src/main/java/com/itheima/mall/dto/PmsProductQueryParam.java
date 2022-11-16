package com.itheima.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class PmsProductQueryParam implements Serializable {

    private Integer publishStatus;
    private Integer verifyStatus;
    private String keyword;
    private String productSn;
    private Long productCategoryId;
    private Long brandId;
}
