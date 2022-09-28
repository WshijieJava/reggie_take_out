package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.mapper.OrderMapper;
import com.itheima.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     * @param orders
     */
    @Transactional
    public void submit(Orders orders) {
        // 1.获得当前用户id
        Long userId = BaseContext.getCurrentId();

        // 2.得到当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(wrapper);

        if(shoppingCarts == null || shoppingCarts.size() == 0){
            // 如果购物车为空,不能下单
            throw new CustomException("购物车为空,不能下单");
        }

        // 3.根据当前登录用户id,查询用户数据
        User user = userService.getById(userId);

        // 4.根据地址id,查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);

        if(addressBook == null){
            // 地址为空,直接抛异常
            throw new CustomException("用户地址信息有误,不能下单");
        }

        // 5.先生成一个唯一的id值,作为订单号
        long orderId = IdWorker.getId();


        // 原子类(安全)
        // 总金额初始值为0
        AtomicInteger amount = new AtomicInteger(0);


        // 组装订单明细数据,批量保存订单明细
        // 遍历购物车数据,将购物车中的数据,依次转化成订单数据
        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            // 将购物车数据转化成订单详情表数据
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());


        // 组装订单数据,批量保存订单数据
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());//下单时间
        orders.setCheckoutTime(LocalDateTime.now());//结账时间

        // 1代表待付款,2代表待发货
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);

        // 订单号=订单id
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());

        // 设置收货人
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());

        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //向订单表插入数据，一条数据
        this.save(orders);

        //向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);

        //清空购物车数据
        shoppingCartService.remove(wrapper);
    }
}