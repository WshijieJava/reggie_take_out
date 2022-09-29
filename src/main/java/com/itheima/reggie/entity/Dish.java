package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 书籍
 */
@ApiModel(value = "书籍",description = "书籍类实体")
@Data
public class Dish implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "id", value = "书籍id")
    private Long id;


    //菜品名称
    @ApiModelProperty(name = "name",value = "书籍名字")
    private String name;


    //菜品分类id
    @ApiModelProperty(name = "categoryId",value = "书籍分类id")
    private Long categoryId;


    //菜品价格
    @ApiModelProperty(name = "price",value = "书籍价格")
    private BigDecimal price;


    //商品码
    @ApiModelProperty(name = "code",value = "书籍商品码")
    private String code;


    //图片
    @ApiModelProperty(name = "image",value = "书籍图片")
    private String image;


    //描述信息
    @ApiModelProperty(name = "description",value = "描述信息")
    private String description;


    //0 停售 1 起售
    @ApiModelProperty(name = "status",value = "售卖状态：0 停售 1 起售")
    private Integer status;


    //顺序
    private Integer sort;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


    @TableField(fill = FieldFill.INSERT)
    private Long createUser;


    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

    // 注意：数据库中要有对应的字段，用来存储删除状态
    @TableLogic(value = "0",delval = "1")
    private Integer isDeleted;
}
