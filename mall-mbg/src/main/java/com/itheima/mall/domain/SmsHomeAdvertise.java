package com.itheima.mall.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author 小刘
 * @since 2022-10-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SmsHomeAdvertise implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private Integer type;

    private LocalDateTime startTime;

    private String url;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private LocalDateTime endTime;

    private Integer status;

    private Integer clickCount;

    private Integer orderCount;
    private String pic;

    private String note;

    private Integer sort;


}
