package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CateGoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CateGoryMapper, Category> implements CategoryService {
    /**
     * 转入要用到的菜品和套餐
     */
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;
    /**
     *
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件,根据分类id查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(dishLambdaQueryWrapper);
        //c查询是否当前分类是否关系菜品,如果已经关系,抛出一个异常
        if (count > 0){
            //已经关联菜品,抛出异常
            throw new CustomException("当前分类关联菜品,不能删除");
        }

        //查询菜品是否关联套餐,如关联,抛出一个异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count();
        if (count2 > 0){
            //已经关联套餐,抛出异常
            throw new CustomException("当前分类下关联了套餐,不能删除");
        }

        //正常删除分类
        super.removeById(id);
    }
}
