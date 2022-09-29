package com.itheima.reggie.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
/**
 * 用户信息
 */
@ApiModel(value = "用户信息")
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "id", value = "用户id")
    private Long id;


    //姓名
    @ApiModelProperty(name = "name", value = "用户姓名")
    private String name;


    //手机号
    @ApiModelProperty(name = "phone", value = "手机号")
    private String phone;


    //性别 0 女 1 男
    @ApiModelProperty(name = "sex", value = "性别 0 女 1 男")
    private String sex;


    //身份证号
    @ApiModelProperty(name = "idNumber", value = "身份证号")
    private String idNumber;


    //头像
    @ApiModelProperty(name = "avatar", value = "头像")
    private String avatar;


    //状态 0:禁用，1:正常
    @ApiModelProperty(name = "status", value = "状态 0:禁用，1:正常")
    private Integer status;
}
