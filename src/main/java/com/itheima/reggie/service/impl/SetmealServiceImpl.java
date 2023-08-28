package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper,Setmeal> implements SetmealService {
    /**
     * 保存套餐同时需要保存套餐和菜品的关联关系
     */
    @Autowired
    private SetmealDishService setmealDishService;

    //setmealDto 是setmeal的超类
    //事务注释,如果发生异常会回滚数据库操作
    @Transactional
    public void saveWithDish(SetmealDto setmealDto){
        //保存套餐的基本西悉尼,操作setmeal 执行insert
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //转为setmealdish格式
        setmealDishes.stream().map((item) ->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //转为setmealdish格式,所对应的service保存
        //保存套餐和菜品的关联西悉尼,操作setmeal_dish,执行inst操作
        //批量保存方法
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * s删除套餐
     */

    @Transactional
    public void removeWithDish(List<Long> ids){
        //查询套餐状态,是否可删除

        //select count(*) from setmeal where id in (1,2,3) and status = 1  123是套餐状态
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);
        if (count > 0){
            //如果不能删除,抛出一个业务异常
            throw new CustomException("套餐正在售卖,不能删除");
        }
        //如果可以删除,先删除套餐表中的数据 --setmeal
        this.removeByIds(ids);


//        不能用setmealdish的批量刹车你,ids转入的是套餐表的id不是套餐菜品关系表的id，不是主键
//        setmealDishService.removeById()

        //delete from setmeal_dish where setmeal_id in (1,2,3)
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);


        //删除套餐与菜品 表中数据
        setmealDishService.remove(lambdaQueryWrapper);
    }
}
