package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

//冗余字段 如套餐菜品name 就不用菜品id可以查出name,但是
    //新增套餐分类在菜品分类前端api已复用,type:1菜品 2套餐分类
    @RestController
    @RequestMapping("/setmeal")
    @Slf4j
    public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     */
    @PostMapping
    //是js格式 用@restquestbody
    //不能是setmeal对象,set2meal没有菜品name id等
    //要传setmealdot超类
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("套餐信息 {}", setmealDto);
        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐成功");

    }

    /**
     * 套餐 分页查询
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //分页查询构造器对象
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        //改造为能显示套餐分类, 用Setmeal套餐分类显示不出来,setmeal只有套餐id.你
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件
        queryWrapper.like(name != null, Setmeal::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Setmeal::getCreateTime);

        setmealService.page(pageInfo, queryWrapper);
        //改造 对象拷贝
        //records 是mybat的page 的字段
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();

        //item流数据给list
        List<SetmealDto> list = records.stream().map((item) -> {

            //自己new的对象属性都是为空,需要beanutils拷数据过来
            SetmealDto setmealDto = new SetmealDto();
            //烤数据
            BeanUtils.copyProperties(item,setmealDto);

            //分类id
            Long categoryId = item.getCategoryId();
            //根据分类id查询对象
            Category category = categoryService.getById(categoryId);
            if (category !=null){
               // 套餐分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);
        return R.success(dtoPage);

    }

    /**
     * 删除套餐 只能删除停售套餐,并且要删除调菜品与套餐关联关系
     * @param ids
     * @return
     */
    @DeleteMapping
    //可能转入多个id,所以是list集合 ,id很长所以long型
//@RequestParam 注解用于提取请求参数的值，并将其绑定到控制器方法中的方法参数上
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("ids {}",ids);


        setmealService.removeWithDish(ids);

        return R.success("套餐数据删除成功");
    }


    /**
     * 客户端 根据条件查询商务和儿童 套餐数据
     * @param
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }
}

