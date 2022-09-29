package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
菜品口味
 */
@ApiModel(value = "书籍标签",description = "每个书籍都有属于自己的标签")
@Data
public class DishFlavor implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "id", value = "标签id")
    private Long id;


    //菜品id
    @ApiModelProperty(name = "dishId", value = "书籍id")
    private Long dishId;


    //口味名称
    @ApiModelProperty(name = "name", value = "标签名字")
    private String name;


    //口味数据list
    @ApiModelProperty(name = "value", value = "标签引用数")
    private String value;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


    @TableField(fill = FieldFill.INSERT)
    private Long createUser;


    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

//
//    //是否删除
//    private Integer isDeleted;

}
