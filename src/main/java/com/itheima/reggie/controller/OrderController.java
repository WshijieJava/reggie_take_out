package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     *
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info("订单数据：{}", orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }

    @GetMapping("/page")
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


    @PutMapping("/dispatch")
    public R<String> dispatch(@RequestBody Orders orders) {
        boolean b = orderService.updateById(orders);
        return b?R.success("派送成功"):R.error("操作失败");
    }


}