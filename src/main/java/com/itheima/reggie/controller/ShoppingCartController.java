package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> addShoppingCart(@RequestBody ShoppingCart shoppingCart){

        //获取当前在操作购物车的用户id
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        //获取当前要加入的套餐或者菜品中的dishId
        Long dishId = shoppingCart.getDishId();

        //创建条件构造器
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,currentId);

        if(dishId!=null){
            lambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);
        }
        else
        {
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        //如果购物车中有过这个套餐或菜品则数量加一，如果没有过则默认number为一
        ShoppingCart cart = shoppingCartService.getOne(lambdaQueryWrapper);
        if(cart!=null){
            Integer number = cart.getNumber();
            cart.setNumber(number+1);
            shoppingCartService.updateById(cart);
        }
        else
        {

            //shoppingCart是一开始传进来的参数
            shoppingCart.setNumber(1);
            //设置创建的时间
            shoppingCart.setCreateTime(LocalDateTime.now());
            cart=shoppingCart;
            shoppingCartService.save(cart);
        }
        return R.success(cart);

    }

    /*
    购物车展示
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){

        //获取当前用户的id
        Long currentId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,currentId);
        lambdaQueryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(lambdaQueryWrapper);
        return R.success(list);

    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        //获取当前用户的id
        Long currentId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,currentId);

        shoppingCartService.remove(lambdaQueryWrapper);
        return R.success("删除成功");
    }

    /*
    减少菜品数量
     */
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart){
        Long dishId = shoppingCart.getDishId();
        Long userId= BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);
        if(dishId!=null){
            lambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);
        }
        else
        {
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart cart = shoppingCartService.getOne(lambdaQueryWrapper);
        Integer number = cart.getNumber();
        cart.setNumber(number-1);
        if(cart.getNumber()==0){
            shoppingCartService.remove(lambdaQueryWrapper);
            return R.success("删除成功");
        }
        shoppingCartService.updateById(cart);
        return R.success("删除成功");

    }


}