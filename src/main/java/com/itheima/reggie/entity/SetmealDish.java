package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 套餐菜品关系
 */
@ApiModel(value = "套餐书籍详情")
@Data
public class SetmealDish implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "id",value = "详情id")
    private Long id;


    //套餐id
    @ApiModelProperty(name = "setmealId",value = "套餐id")
    private Long setmealId;


    //菜品id
    @ApiModelProperty(name = "dishId",value = "书籍id")
    private Long dishId;


    //菜品名称 （冗余字段）
    @ApiModelProperty(name = "name",value = "书籍名称")
    private String name;

    //菜品原价
    @ApiModelProperty(name = "price",value = "书籍原价")
    private BigDecimal price;

    //份数
    @ApiModelProperty(name = "copies",value = "套餐数量")
    private Integer copies;


    //排序
    private Integer sort;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


    @TableField(fill = FieldFill.INSERT)
    private Long createUser;


    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;


    //是否删除
    private Integer isDeleted;
}
