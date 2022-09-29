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
 * 套餐
 */
@ApiModel(value = "套餐信息")
@Data
public class Setmeal implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "id", value = "套餐id")
    private Long id;


    //分类id
    @ApiModelProperty(name = "categoryId", value = "分类id")
    private Long categoryId;


    //套餐名称
    @ApiModelProperty(name = "name", value = "套餐名称")
    private String name;


    //套餐价格
    @ApiModelProperty(name = "price", value = "套餐价格")
    private BigDecimal price;


    //状态 0:停用 1:启用
    @ApiModelProperty(name = "status", value = "套餐状态 0:停用 1:启用")
    private Integer status;


    //编码
    @ApiModelProperty(name = "code", value = "套餐编码")
    private String code;


    //描述信息
    @ApiModelProperty(name = "description", value = "套餐描述信息")
    private String description;


    //图片
    @ApiModelProperty(name = "image", value = "图片")
    private String image;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


    @TableField(fill = FieldFill.INSERT)
    private Long createUser;


    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
