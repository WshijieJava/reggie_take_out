package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 员工实体
 */
@ApiModel(value = "商户信息")
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "id",value = "商户id")
    private Long id;

    @ApiModelProperty(name = "username",value = "商户属主名")
    private String username;

    @ApiModelProperty(name = "name",value = "商户名")
    private String name;

    @ApiModelProperty(name = "password",value = "商家登入密码")
    private String password;

    @ApiModelProperty(name = "phone",value = "商家电话号码")
    private String phone;

    @ApiModelProperty(name = "sex",value = "商家性别")
    private String sex;

    @ApiModelProperty(name = "idNumber",value = "商家身份证号码")
    private String idNumber;//身份证号码

    @ApiModelProperty(name = "status",value = "商家状态")
    private Integer status;

    @TableField(fill = FieldFill.INSERT) //插入时填充字段
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE) //插入和更新时填充字段
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT) //插入时填充字段
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE) //插入和更新时填充字段
    private Long updateUser;

}
