package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.service.*;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单
 */
@Api(tags = "订单管理接口")
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    DishService dishService;

    @Autowired
    ShoppingCartService shoppingCartService;

    @Autowired
    OrderDetailService orderDetailService;

    @Autowired
    SetmealService setmealService;

    /**
     * 用户下单
     *
     * @param orders
     * @return
     */
    @ApiOperation(value = "用户下单")
    @PostMapping("/submit")
    public R<String> submit(
            @ApiParam(name = "orders", required = true, value = "订单信息")
            @RequestBody Orders orders) {
        log.info("订单数据：{}", orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }

    @GetMapping("/page")
    @ApiOperation(value = "查询一段时间内的订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页号"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页面大小"),
            @ApiImplicitParam(name = "number", required = true, value = "订单数"),
            @ApiImplicitParam(name = "beginTime", required = true, value = "开始时间"),
            @ApiImplicitParam(name = "endTime", required = true, value = "结束时间")
    })
    public R<Page<Orders>> page(Integer page, Integer pageSize, String number, String beginTime, String endTime) {
        log.info("page:{},pageSize:{},number:{},beginTime:{},endTime:{}", page, pageSize, number, beginTime, endTime);

        Page<Orders> ordersPage = new Page<>(page, pageSize);

        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        //模糊匹配订单号
        wrapper.like(number != null, Orders::getNumber, number);
        //匹配订单时间
        wrapper.between(beginTime != null, Orders::getOrderTime, beginTime, endTime);

        orderService.page(ordersPage);
        return R.success(ordersPage);
    }


    @ApiOperation(value = "订单派送")
    @PutMapping("/dispatch")
    public R<String> dispatch(
            @ApiParam(name = "orders", required = true, value = "订单信息")
            @RequestBody Orders orders) {
        boolean b = orderService.updateById(orders);
        return b ? R.success("派送成功") : R.error("操作失败");
    }


    @GetMapping("/userPage")
    @ApiOperation("用户订单详情页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页号"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页面大小")
    })
    public R<Page<Orders>> page(@RequestParam("page") int page, int pageSize, HttpSession session) {
        log.info("page = {},pageSize = {}", page, pageSize);

        Long userId = (Long) session.getAttribute("user");

        Page<Orders> OrderPage = new Page<>(page, pageSize);

        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(Orders::getUserId, userId);
        wrapper.orderByDesc(Orders::getOrderTime);

        orderService.page(OrderPage, wrapper);
        return R.success(OrderPage);
    }


    /**
     * 再来一单
     */
    @ApiOperation("再来一单")
    @PostMapping("/again")
    public R<String> again(
            @ApiParam(name = "orders", required = true, value = "历史订单")
            @RequestBody Orders orders) {
        log.info(orders.toString());
        //设置用户id 指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        //得到订单id
        Long ordersId = orders.getId();
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getId, ordersId);
        //根据订单id得到订单元素
        Orders one = orderService.getOne(queryWrapper);
        //得到订单表中的number 也就是订单明细表中的order_id
        String number = one.getNumber();

        LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OrderDetail::getOrderId, number);
        //根据订单明细表的order_id得到订单明细集合
        List<OrderDetail> orderDetails = orderDetailService.list(lambdaQueryWrapper);
        //新建购物车集合
        List<ShoppingCart> shoppingCarts = new ArrayList<>();
        //通过用户id把原来的购物车给清空
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
        //遍历订单明细集合,将集合中的元素加入购物车集合
        for (OrderDetail orderDetail : orderDetails) {
            ShoppingCart shoppingCart = new ShoppingCart();
            //得到菜品id或套餐id
            Long dishId = orderDetail.getDishId();
            Long setmealId = orderDetail.getSetmealId();
            //添加购物车部分属性
            shoppingCart.setUserId(currentId);
            shoppingCart.setDishFlavor(orderDetail.getDishFlavor());
            shoppingCart.setNumber(orderDetail.getNumber());
            shoppingCart.setAmount(orderDetail.getAmount());
            shoppingCart.setCreateTime(LocalDateTime.now());
            if (dishId != null) {
                //订单明细元素中是菜品
                LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
                dishLambdaQueryWrapper.eq(Dish::getId, dishId);
                //根据订单明细集合中的单个元素获得单个菜品元素
                Dish dishone = dishService.getOne(dishLambdaQueryWrapper);
                //根据菜品信息添加购物车信息
                shoppingCart.setDishId(dishId);
                shoppingCart.setName(dishone.getName());
                shoppingCart.setImage(dishone.getImage());
                //调用保存购物车方法
                shoppingCarts.add(shoppingCart);
            } else if (setmealId != null) {
                //订单明细元素中是套餐
                LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
                setmealLambdaQueryWrapper.eq(Setmeal::getId, setmealId);
                //根据订单明细集合中的单个元素获得单个套餐元素
                Setmeal setmealone = setmealService.getOne(setmealLambdaQueryWrapper);
                //根据套餐信息添加购物车信息
                shoppingCart.setSetmealId(setmealId);
                shoppingCart.setName(setmealone.getName());
                shoppingCart.setImage(setmealone.getImage());
                //调用保存购物车方法
                shoppingCarts.add(shoppingCart);
            }
        }
        shoppingCartService.saveBatch(shoppingCarts);
        return R.success("操作成功");
    }


}