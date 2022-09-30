package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrderDto;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.*;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private DishService dishService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private SetmealService setmealService;

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
    public R<Page<Orders>> page(@RequestParam("page") int page, int pageSize, String number, String beginTime, String endTime) {
        log.info("page = {},pageSize = {},number={},beginTime={},endTime={}", page, pageSize, number, beginTime, endTime);

        //构建分页条件
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        //模糊搜索
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(number != null, Orders::getNumber, number);
        wrapper.ge(beginTime != null, Orders::getOrderTime, beginTime);
        wrapper.le(endTime != null, Orders::getOrderTime, endTime);

        //执行查询
        orderService.page(ordersPage, wrapper);

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


    //抽离的一个方法，通过订单id查询订单明细，得到一个订单明细的集合
    //这里抽离出来是为了避免在stream中遍历的时候直接使用构造条件来查询导致eq叠加，从而导致后面查询的数据都是null
    public List<OrderDetail> getOrderDetailListByOrderId(Long orderId){
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId, orderId);
        List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper);
        return orderDetailList;
    }

    @GetMapping("/userPage")
    @ApiOperation("用户订单详情页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页号"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页面大小")
    })
    public R<Page> page(int page, int pageSize){
        //分页构造器对象
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrderDto> pageDto = new Page<>(page,pageSize);
        //构造条件查询对象
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId,BaseContext.getCurrentId());
        //这里是直接把当前用户分页的全部结果查询出来，要添加用户id作为查询条件，否则会出现用户可以查询到其他用户的订单情况
        //添加排序条件，根据更新时间降序排列
        queryWrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(pageInfo,queryWrapper);

        //通过OrderId查询对应的OrderDetail
        LambdaQueryWrapper<OrderDetail> queryWrapper2 = new LambdaQueryWrapper<>();

        //对OrderDto进行需要的属性赋值
        List<Orders> records = pageInfo.getRecords();
        List<OrderDto> orderDtoList = records.stream().map((item) ->{
            OrderDto orderDto = new OrderDto();
            //此时的orderDto对象里面orderDetails属性还是空 下面准备为它赋值
            Long orderId = item.getId();//获取订单id
            List<OrderDetail> orderDetailList = this.getOrderDetailListByOrderId(orderId);
            BeanUtils.copyProperties(item,orderDto);
            //对orderDto进行OrderDetails属性的赋值
            orderDto.setOrderDetails(orderDetailList);
            return orderDto;
        }).collect(Collectors.toList());

        //使用dto的分页有点难度.....需要重点掌握
        BeanUtils.copyProperties(pageInfo,pageDto,"records");
        pageDto.setRecords(orderDtoList);
        return R.success(pageDto);
    }
//    public R<Page<Orders>> page(@RequestParam("page") int page, int pageSize, HttpSession session) {
//        log.info("page = {},pageSize = {}", page, pageSize);
//
//        Long userId = (Long) session.getAttribute("user");
//
//        Page<Orders> OrderPage = new Page<>(page, pageSize);
//
//        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
//
//        wrapper.eq(Orders::getUserId, userId);
//        wrapper.orderByDesc(Orders::getOrderTime);
//
//        orderService.page(OrderPage, wrapper);
//        return R.success(OrderPage);
//    }


    /**
     * 再来一单
     * 前端点击再来一单是直接跳转到购物车的，所以为了避免数据有问题，再跳转之前我们需要把购物车的数据给清除
     * ①通过orderId获取订单明细
     * ②把订单明细的数据的数据塞到购物车表中，不过在此之前要先把购物车表中的数据给清除(清除的是当前登录用户的购物车表中的数据)，
     * 不然就会导致再来一单的数据有问题；
     */
    @ApiOperation("再来一单")
    @PostMapping("/again")
    public R<String> againSubmit(
            @ApiParam(name = "orders", required = true, value = "历史订单")
            @RequestBody Map<String, String> map) {
        String ids = map.get("id");

        long id = Long.parseLong(ids);

        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId, id);
        //获取该订单对应的所有的订单明细表
        List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper);

        //通过用户id把原来购物车给清空
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);

        //获取用户id
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map((item) -> {
            //把从order表中和order_details表中获取到的数据赋值给这个购物车对象
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUserId(userId);
            shoppingCart.setImage(item.getImage());
            Long dishId = item.getDishId();
            Long setmealId = item.getSetmealId();
            if (dishId != null) {
                //如果是菜品那就添加菜品的查询条件
                shoppingCart.setDishId(dishId);
            } else {
                //添加到购物车的是套餐
                shoppingCart.setSetmealId(setmealId);
            }
            shoppingCart.setName(item.getName());
            shoppingCart.setDishFlavor(item.getDishFlavor());
            shoppingCart.setNumber(item.getNumber());
            shoppingCart.setAmount(item.getAmount());
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());

        //把携带数据的购物车批量插入购物车表  这个批量保存的方法要使用熟练！！！
        shoppingCartService.saveBatch(shoppingCartList);

        return R.success("操作成功");
    }

}