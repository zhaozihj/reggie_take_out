package com.itheima.reggie.dto;

import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
//继承了Dish之后就可以接受Dish中属性的参数
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    //categoryName是菜品分类的名称
    private String categoryName;

    private Integer copies;
}
