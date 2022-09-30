package com.itheima.reggie.dto;

import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import lombok.Data;

import java.util.List;

/**
 * @author 王仕杰
 * @version 1.0
 */
@Data
public class OrderDto extends Orders {
    private List<OrderDetail> orderDetails;
}
