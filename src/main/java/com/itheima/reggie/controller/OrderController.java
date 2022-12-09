package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomerException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrderDto;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    /*
    用户下单
     */

    @PostMapping("/submit")
    public R<String> submit(  @RequestBody Orders orders){

        orderService.submit(orders);
        return R.success("下单成功");
    }

    @GetMapping("/userPage")
    public R<Page<OrderDto>> page(int page, int pageSize){
        Page<Orders> pageInfo=new Page<>(page,pageSize);
        Page<OrderDto> orderDtoPage=new Page<>();
        Long id = BaseContext.getCurrentId();
        LambdaQueryWrapper<Orders> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Orders::getUserId,id);
        orderService.page(pageInfo,lambdaQueryWrapper);

        BeanUtils.copyProperties(pageInfo,orderDtoPage,"records");

        List<Orders> orders = pageInfo.getRecords();

        List<OrderDto> orderDtos=new ArrayList<>();
        for (Orders order : orders) {
            OrderDto orderDto=new OrderDto();

            //获得订单编号
            Long orderId = order.getId();

            LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper1=new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.eq(OrderDetail::getOrderId,orderId);
            //查询多个都是使用list
            List<OrderDetail> orderDetails = orderDetailService.list(lambdaQueryWrapper1);
            orderDto.setOrderDetails(orderDetails);

            BeanUtils.copyProperties(order,orderDto);
            orderDtos.add(orderDto);
        }
        orderDtoPage.setRecords(orderDtos);
        return R.success(orderDtoPage);
    }

}