package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomerException;
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

import java.util.Arrays;
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

        //删除原本口味表中的记录
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<DishFlavor>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(lambdaQueryWrapper);

        Long Dishid=dishDto.getId();
        //这个flavors中的DishFlavor中的dishid都没有被赋值
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(Dishid);
        }

        //批量对flavor表中进行插入
        dishFlavorService.saveBatch(flavors);

    }

    //删除记录
    @Override
    public void delete(Long[] ids) {
        //查询套餐状态，确定是否真的要删除
        //条件构造器中的条件是指套餐id在这些参数里面，套餐的状态还是可用状态的
        //就是可用状态的套餐是不能够随便删除的
        LambdaQueryWrapper<Dish> lambdaQueryWrapper1=new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.in(Dish::getId,ids);
        lambdaQueryWrapper1.eq(Dish::getStatus,1);

        //返回符合这个条件构造器的记录的个数
        int count=this.count(lambdaQueryWrapper1);
        if(count>0){
            throw new CustomerException("套餐正在售卖中，不能删除");
        }



        //删除dish表中数据
        //这里removeByIds只能用集合来删除，但接收参数只能用数组
        List<Long> list = Arrays.asList(ids);
        dishService.removeByIds(list);

        //删除dishflavor表中的数据
        for (Long id : ids) {
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,id);
            dishFlavorService.remove(lambdaQueryWrapper);
        }
    }




}
