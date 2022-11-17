package com.itheima.mall.dto;

import com.itheima.mall.domain.PmsProductCategory;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PmsProductCategoryParam extends PmsProductCategory implements Serializable {
    private List<Long>ids;
}
