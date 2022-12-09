
package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

public interface SetmealService extends IService<Setmeal>  {

    public void  saveWithDish(SetmealDto setmealDto);

    //删除功能
    public void deleteWithDish(Long ids[]);
}