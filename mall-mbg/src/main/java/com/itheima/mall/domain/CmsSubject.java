package com.itheima.mall.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 专题表
 * </p>
 *
 * @author 小刘
 * @since 2022-11-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CmsSubject implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long categoryId;

    private String title;

    /**
     * 专题主图
     */
    private String pic;

    /**
     * 关联产品数量
     */
    private Integer productCount;

    private Integer recommendStatus;

    private LocalDateTime createTime;

    private Integer collectCount;

    private Integer readCount;

    private Integer commentCount;

    /**
     * 画册图片用逗号分割
     */
    private String albumPics;

    private String description;

    /**
     * 显示状态：0->不显示；1->显示
     */
    private Integer showStatus;

    private String content;

    /**
     * 转发数
     */
    private Integer forwardCount;

    /**
     * 专题分类名称
     */
    private String categoryName;


}
