package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/*
套餐管理
 */
@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    @Transactional
//多个事务要进行事务管理
    //如果新插入了一个套餐，那么之前的所有套餐缓存也就是所有setmealCache中的缓存都会被删除
    @CacheEvict(value="setmealCache",allEntries = true)
    public R<String> insert(@RequestBody SetmealDto setmealDto){

        setmealService.saveWithDish(setmealDto);
        return R.success("保存成功");
    }

    //分页查询功能的实现
    //这个和之前那个菜品类型管理的分页查询功能类似
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){

        Page<Setmeal> setmealPage=new Page<Setmeal> (page,pageSize);
        Page<SetmealDto> setmealDtoPage=new Page<>();
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper=new LambdaQueryWrapper<>();

        //添加排序条件，根据更新时间进行like模糊查询
        lambdaQueryWrapper.orderByDesc(Setmeal::getCreateTime);
        //添加查询条件，根据立刻进行模糊查询
        lambdaQueryWrapper.like(name!=null,Setmeal::getName,name);
        setmealService.page(setmealPage,lambdaQueryWrapper);

        //把setmealPage中除了records属性都赋值给setmealDtoPage
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");

        //下面这些是因为Setmeal对象中没有SermealDto所需要的categoryName，下面是在获取categoryName
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> records2=new ArrayList<>();
        for (Setmeal record : records) {
            SetmealDto setmealDto=new SetmealDto();
            BeanUtils.copyProperties(record,setmealDto);

            //这个是这个具体的套餐对应的套餐类型的id
            Long categoryId = record.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category!=null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            records2.add(setmealDto);
        }
        setmealDtoPage.setRecords(records2);

        return R.success(setmealDtoPage);
    }

    @DeleteMapping
    //当一个套餐被删除之后，setmealCache中也就是所有套餐的缓存都会被删除
    @CacheEvict(value="setmealCache",allEntries = true)
    @Transactional
        public R<String> delete(Long ids[]){

        //删除操作
        setmealService.deleteWithDish(ids);

        return R.success("删除成功");
    }

    @PostMapping("/status/{status}")
    public R<String> editStatus(Long ids[],@PathVariable int status){

        for (Long id : ids) {
            LambdaUpdateWrapper<Setmeal> lambdaUpdateWrapper=new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(Setmeal::getId,id);
            lambdaUpdateWrapper.set(Setmeal::getStatus,status);
            setmealService.update(lambdaUpdateWrapper);
        }
        return R.success("修改成功");
    }


    @GetMapping("/list")
    //#setmeal拿到方法中传递的参数,然后这个key也是用setmeal.categoryId来做的，根据套餐分类缓存
    @Cacheable(value="setmealCache",key="#setmeal.categoryId+'_'+#setmeal.status")
    public R<List<Setmeal>> list (Setmeal setmeal) {

        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Setmeal::getCategoryId, setmeal.getCategoryId());
        lambdaQueryWrapper.eq(Setmeal::getStatus, 1);
        lambdaQueryWrapper.orderByAsc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(lambdaQueryWrapper);
        return R.success(list);

    }

}