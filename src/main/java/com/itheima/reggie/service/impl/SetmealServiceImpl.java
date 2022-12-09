package com.itheima.reggie.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomerException;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //先保存到setmeal表
        setmealService.save(setmealDto);
        //保存之后的setmealDto对象就会自动有id，就是具体套餐的id
        Long setmealid = setmealDto.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //这里的SetmealDish没有接收到SetmealId
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealid);
        }

        //对setmealdish表进行保存
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    public void deleteWithDish(Long ids[]) {

        //查询套餐状态，确定是否真的要删除
        //条件构造器中的条件是指套餐id在这些参数里面，套餐的状态还是可用状态的
        //就是可用状态的套餐是不能够随便删除的
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Setmeal::getId,ids);
        lambdaQueryWrapper.eq(Setmeal::getStatus,1);

        //返回符合这个条件构造器的记录的个数
        int count=this.count(lambdaQueryWrapper);
        if(count>0){
            throw new CustomerException("套餐正在售卖中，不能删除");
        }

        //将long类型的数组转换为集合
        List<Long> list =  Arrays.asList(ids);

        //从setmeal表中删除记录
        setmealService.removeByIds(list);

        //从setmealdish表示删除数据
   /* LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper2=new LambdaQueryWrapper<>();
    lambdaQueryWrapper2.eq(SetmealDish::getDishId,ids);
    setmealDishService.remove(lambdaQueryWrapper2);*/

        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper1=new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.in(SetmealDish::getDishId,ids);
        //删除关系表中的数据----setmeal_dish
        //删除所有dishid在ids中的以及setmeal_dish表中的记录
        setmealDishService.remove(lambdaQueryWrapper1);


    }

}