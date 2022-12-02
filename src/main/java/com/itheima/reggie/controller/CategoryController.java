package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.impl.CategoryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
//这里面请求都是这个开头的
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;
    @PostMapping
      public R<String> insert(@RequestBody Category category){
          log.info("category{}",category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    @GetMapping("/page")
    public R<Page> select(int page,int pageSize){
        Page<Category> pageInfo=new Page<>(page,pageSize);

        //条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(Category::getSort);
        //这个方法发起sql语句的时候是根据这个Category类中的属性来执行sql语句的
        categoryService.page(pageInfo,lambdaQueryWrapper);
        log.info(pageInfo.toString());
        return R.success(pageInfo);
    }

    @DeleteMapping
    //id已经变为String类型的了
    public R<String> delete( Long ids){
        log.info("id:{}",ids);

     categoryService.remove(ids);
     return R.success("删除成功了");
    }

    @PutMapping
    public R<String> update(@RequestBody  Category category){
        categoryService.updateById(category);
        return R.success("修改完成");
    }


    /*
    新增菜品的时候，点击新增菜品，查询菜品分类在下拉框里
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //创建条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        //添加条件
        lambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //添加排序条件
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        //把搜索出来的放在list集合中
        List<Category> list=categoryService.list(lambdaQueryWrapper);


        return R.success(list);
    }

}
