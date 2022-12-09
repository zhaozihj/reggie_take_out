package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.impl.DishServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    //菜品
    @Autowired
    private DishService dishService;


    @Autowired
    private CategoryService categoryService;

    //口味
    @Autowired
    private DishFlavorService dishFlavorService;

    @PostMapping
    public R<String> save( @RequestBody DishDto dishDto){
        //这个save里除了是Dish类是Dish类的子类也是可以的
        //dishService.save(dishDto);
         dishService.saveWithDishFlavor(dishDto);
         return R.success("保存成功");


    }

    /*
    查询方法
     */

    //因为Dish对象中的菜品类型是以id存储的，页面需要显示的是名称，所以要把page对象中的records换一下
    //要新建一个分页对象，其他属性和Dish那个分页对象一样，只有records要进行修改，把里面内容换为DishDto对象的内容
    //主要是因为DishDto没有自己的Service，像这种查询的dishService必须要求这个条件构造器是Dish泛型的，所以就得向上面这样分布搞
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Dish> pageInfo=new Page<>(page,pageSize);
        Page<DishDto> dishDto=new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper<Dish>();
         lambdaQueryWrapper.like(name != null, Dish::getName, name);
        dishService.page(pageInfo,lambdaQueryWrapper);

        //把pageInfo对象中除了records属性都拷贝一份到dishDto对象中
        BeanUtils.copyProperties(pageInfo,dishDto,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> records2=new ArrayList<>();
        for (Dish record : records) {
            Long categoryId = record.getCategoryId();
            Category category = categoryService.getById(categoryId);
            DishDto dishDto1=new DishDto();
            if(category!=null) {
                String categoryName = category.getName();
                dishDto1.setCategoryName(categoryName);
            }

            BeanUtils.copyProperties(record,dishDto1);

            records2.add(dishDto1);
        }

        dishDto.setRecords(records2);
        return R.success(dishDto);

    }

    /*
    再修改页面上获取原本的参数值
     */
    @GetMapping("/{id}")
    public R<DishDto> edit(@PathVariable long id){
       DishDto dishDto=dishService.get(id);
        return R.success(dishDto);

    }

    /*
    提交修改之后的数据
     */
    @PutMapping
    public R<String> editPlus( @RequestBody DishDto dishDto){
        dishService.updateWithDishFlavor(dishDto);
        return R.success("修改成功");
    }

    /*
删除对应的信息
 */
    @DeleteMapping
    @Transactional
    public R<String> delete( Long ids[]){

        dishService.delete(ids);
        return R.success("删除成功了");
    }

    /*
改变商品功能的信息
 */
    @PostMapping("/status/{status}")
    public R<String> editStatus(Long ids[],@PathVariable int status){

        for (Long id : ids) {
            //创建一个菜品对象,并把获得的id赋值给这个对象
            Dish dish = new Dish();
            dish.setId(id);

            //将获取的id存放在dish对象中，并且设置status为传递的参数
            dish.setStatus(status);

            //对status进行修改，创建时间和创建人什么的只写都是写过自动的
            dishService.updateById(dish);

        }
        return R.success("修改成功");
    }



    /*
    这个功能是在套餐管理添加的页面中，查询每一种菜品类型所对应的全部具体的菜品
 */
    //用dish来接受catogoryId
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){

        LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //查询status为1的菜品，就是正在起售的菜品
        lambdaQueryWrapper.eq(Dish::getStatus,1);

        //添加排序条件
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lambdaQueryWrapper);
        List<DishDto> listDto=new ArrayList<>();
        for (Dish dish1 : list) {
            Long categoryId = dish1.getCategoryId();
            Category category = categoryService.getById(categoryId);
            DishDto dishDto1=new DishDto();
            if(category!=null) {
                String categoryName = category.getName();
                dishDto1.setCategoryName(categoryName);
            }

            //通过具体菜品的id，查出它的口味
            Long dishId=dish1.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper1=new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavorList=dishFlavorService.list(lambdaQueryWrapper1);
            dishDto1.setFlavors(dishFlavorList);


            BeanUtils.copyProperties(dish1,dishDto1);
            listDto.add(dishDto1);
        }

        return R.success(listDto);
    }
}
