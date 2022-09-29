package com.itheima.reggie.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单
 */
@ApiModel(value = "订单信息")
@Data
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "id", value = "订单id")
    private Long id;

    //订单号
    @ApiModelProperty(name = "number",value = "number")
    private String number;

    //订单状态 1待付款，2待派送，3已派送，4已完成，5已取消
    @ApiModelProperty(name = "status",value = "订单状态 1待付款，2待派送，3已派送，4已完成，5已取消")
    private Integer status;


    //下单用户id
    @ApiModelProperty(name = "userId",value = "下单用户id")
    private Long userId;

    //地址id
    @ApiModelProperty(name = "addressBookId",value = "地址id")
    private Long addressBookId;


    //下单时间
    @ApiModelProperty(name = "orderTime",value = "下单时间")
    private LocalDateTime orderTime;


    //结账时间
    @ApiModelProperty(name = "checkoutTime",value = "结账时间")
    private LocalDateTime checkoutTime;


    //支付方式 1\微信，2支付宝
    @ApiModelProperty(name = "payMethod",value = "支付方式 1微信，2支付宝")
    private Integer payMethod;


    //实收金额
    @ApiModelProperty(name = "amount",value = "实收金额")
    private BigDecimal amount;

    //备注
    @ApiModelProperty(name = "remark",value = "备注")
    private String remark;

    //用户名
    @ApiModelProperty(name = "userName",value = "用户名")
    private String userName;

    //手机号
    @ApiModelProperty(name = "phone",value = "手机号")
    private String phone;

    //地址
    @ApiModelProperty(name = "address",value = "地址")
    private String address;

    //收货人
    @ApiModelProperty(name = "consignee",value = "收货人")
    private String consignee;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAddressBookId() {
        return addressBookId;
    }

    public void setAddressBookId(Long addressBookId) {
        this.addressBookId = addressBookId;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }

    public LocalDateTime getCheckoutTime() {
        return checkoutTime;
    }

    public void setCheckoutTime(LocalDateTime checkoutTime) {
        this.checkoutTime = checkoutTime;
    }

    public Integer getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(Integer payMethod) {
        this.payMethod = payMethod;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }
}
