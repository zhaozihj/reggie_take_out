package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;


public interface DishService extends IService<Dish> {
    //保存到dish中同时保存到flavor表中
    public void saveWithDishFlavor(DishDto dishDto);

    //修改dish表的同时修改flavor表
    public void updateWithDishFlavor(DishDto dishDto);

    //查找要修改的记录的数据显示到修改页面上
    public DishDto get(Long id);

    //删除记录
    public  void delete(Long ids[]);
}
