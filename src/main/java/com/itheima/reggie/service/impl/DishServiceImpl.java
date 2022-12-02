package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Override
    //这里涉及到多个事务，所以用注解开启事务管理
    //这里是遇到所有的异常都回滚的意思
    @Transactional(rollbackFor = Exception.class)
    public void saveWithDishFlavor(DishDto dishDto) {

        //保存dish的属性
        this.save(dishDto);

        List<DishFlavor> flavors = dishDto.getFlavors();
        //获取菜品所属的类的id
        Long id = dishDto.getId();
        //给每一个flavor复制
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(id);
        }
        //批量引入
        dishFlavorService.saveBatch(flavors);

    }


    /*
    获取要修改的菜品的信息
     */
    @Override
    public DishDto get(Long id) {
        //获取菜品对象
        Dish dish = dishService.getById(id);

        //获取菜品对应的类型id
        Long categoryId = dish.getCategoryId();

        //获取菜品类型
        Category category = categoryService.getById(categoryId);

        //获取类型的名称
        String name = category.getName();

        //把dish中的属性都赋值到dishDto中
        DishDto dishDto=new DishDto();
        dishDto.setCategoryName(name);
        BeanUtils.copyProperties(dish,dishDto);

        //条件构造器
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<DishFlavor>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,id);

        //获取dishDto中的flavors属性的值
        List<DishFlavor> list = dishFlavorService.list(lambdaQueryWrapper);

        dishDto.setFlavors(list);
        return dishDto;
    }


    /*
    修改菜品的方法
     */
    @Override
    //既要对dish表进行修改，也要对dishFlavor表进行修改
    public void updateWithDishFlavor(  DishDto dishDto){

       //把dish表先修改了
        //dishDto是Dish的子类的对象，也可以进行修改
        //这个update是根据id修改
        this.updateById(dishDto);

        Long id=dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(id);
        }
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.update(lambdaQueryWrapper);



    }


}
