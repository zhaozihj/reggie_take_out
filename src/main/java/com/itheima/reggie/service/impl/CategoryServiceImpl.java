package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomerException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    DishServiceImpl dishService;
    @Autowired
    SetmealService setmealService;

    @Override
    public void remove(Long ids) {
        //查出这个ids有套餐与之关联抛出异常

        //条件构造器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
         lambdaQueryWrapper.eq(Dish::getCategoryId, ids);
       //SELECT COUNT( * ) FROM dish WHERE (category_id = ?)
        int count = dishService.count(lambdaQueryWrapper);

        if(count>0){
            //抛出异常
            throw new CustomerException("这个分类有菜品与之关联");
        }

        //查出这个ids有套餐与之关联抛出异常
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper1=new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.eq(Setmeal::getCategoryId, ids);
        int count1 = setmealService.count(lambdaQueryWrapper1);

        if(count1>0){
            //抛出异常
            throw new CustomerException("这个分类有套餐与之关联");
        }

        //正常删除
        super.removeById(ids);

    }
}
