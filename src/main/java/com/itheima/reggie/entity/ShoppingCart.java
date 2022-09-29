package com.itheima.reggie.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车
 */
@Data
@ApiModel(value = "购物车")
public class ShoppingCart implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "id", value = "购物车id")
    private Long id;

    //名称
    @ApiModelProperty(name = "name", value = "购物车名称")
    private String name;

    //用户id
    @ApiModelProperty(name = "userId", value = "用户id")
    private Long userId;

    //菜品id
    @ApiModelProperty(name = "userId", value = "书籍id")
    private Long dishId;

    //套餐id
    @ApiModelProperty(name = "setmealId", value = "套餐id")
    private Long setmealId;

    //口味
    @ApiModelProperty(name = "dishFlavor", value = "标签id")
    private String dishFlavor;

    //数量
    @ApiModelProperty(name = "number", value = "商品数量")
    private Integer number;

    //金额
    @ApiModelProperty(name = "amount", value = "商品金额")
    private BigDecimal amount;

    //图片
    @ApiModelProperty(name = "image", value = "图片")
    private String image;

    private LocalDateTime createTime;
}
