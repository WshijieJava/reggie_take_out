package com.itheima.reggie.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单明细
 */
@ApiModel(value = "订单明细")
@Data
public class OrderDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "id",value = "订单详情id")
    private Long id;

    //名称
    @ApiModelProperty(name = "name",value = "订单名称")
    private String name;

    //订单id
    @ApiModelProperty(name = "orderId",value = "订单id")
    private Long orderId;


    //菜品id
    @ApiModelProperty(name = "dishId",value = "书籍id")
    private Long dishId;


    //套餐id
    @ApiModelProperty(name = "setmealId",value = "套餐id")
    private Long setmealId;


    //口味
    @ApiModelProperty(name = "dishFlavor",value = "标签")
    private String dishFlavor;


    //数量
    @ApiModelProperty(name = "number",value = "数量")
    private Integer number;

    //金额
    @ApiModelProperty(name = "amount",value = "金额")
    private BigDecimal amount;

    //图片
    @ApiModelProperty(name = "image",value = "图片")
    private String image;
}
