package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 分类
 */
@Data
@ApiModel(value = "书籍分类",description = "用于设置书籍的分类信息")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "id",value = "分类id")
    private Long id;


    //类型 1 书籍分类 2 套餐分类
    @ApiModelProperty(name = "type",value = "类型 1 书籍分类 2 套餐分类")
    private Integer type;


    //分类名称
    @ApiModelProperty(name = "name",value = "分类名称")
    private String name;


    //顺序
    @ApiModelProperty(name = "sort",value = "顺序")
    private Integer sort;


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

}
