package com.itheima.reggie.dto;

import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * dto 封装页面提交的数据 因为传输的数据与数据库字段不是一一对应的
 */
@Data
public class DishDto extends Dish {

    //flavors前端格式 flavors: [{name:"甜",vlaue:"{"无糖","",}"}]
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
