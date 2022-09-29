package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 地址簿
 */
@ApiModel(value = "地址薄",description = "记录用户的地址信息")
@Data
public class AddressBook implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "id",value = "地址id")
    private Long id;


    //用户id
    @ApiModelProperty(name = "userId",value = "地址关联的用户id")
    private Long userId;


    //收货人
    @ApiModelProperty(name = "consignee",value = "收货人")
    private String consignee;


    //手机号
    @ApiModelProperty(name = "phone",value = "用户电话号码")
    private String phone;


    //性别 0 女 1 男
    @ApiModelProperty(name = "sex",value = "用户性别")
    private String sex;


    //省级区划编号
    @ApiModelProperty(name = "rovinceCode",value = "省级区划编号")
    private String provinceCode;


    //省级名称
    @ApiModelProperty(name = "provinceName",value = "省级名称")
    private String provinceName;


    //市级区划编号
    @ApiModelProperty(name = "cityCode",value = "市级区划编号")
    private String cityCode;


    //市级名称
    @ApiModelProperty(name = "cityName",value = "市级名称")
    private String cityName;


    //区级区划编号
    @ApiModelProperty(name = "districtCode",value = "区级区划编号")
    private String districtCode;


    //区级名称
    @ApiModelProperty(name = "districtName",value = "区级名称")
    private String districtName;


    //详细地址
    @ApiModelProperty(name = "detail",value = "详细地址")
    private String detail;


    //标签
    @ApiModelProperty(name = "label",value = "标签")
    private String label;

    //是否默认 0 否 1是
    @ApiModelProperty(name = "isDefault",value = "是否默认 0 否 1是")
    private Integer isDefault;

    //创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


    //更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


    //创建人
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;


    //修改人
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;


    //是否删除
    private Integer isDeleted;
}
