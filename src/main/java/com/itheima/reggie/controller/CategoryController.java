package com.itheima.reggie.controller;

import ch.qos.logback.classic.spi.STEUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理,
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    /**
     * 新增菜品分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category:{}",category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 菜品分页查询
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        //分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件,根据sort进行排序
        queryWrapper.orderByAsc(Category::getSort);

        //分页查询
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据id删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long id){
        log.info("删除分类,id为:{}", id);

        //还需要判断是否关联菜品,关联菜品不能删
//        categoryService.removeById(id);
        categoryService.remove(id);
        return R.success("分类信息删除成功");
    }

    /**
     *修改时间 修改人可以通过元数据修改器实现
     * @param category
     * @return
     */
    @PutMapping
    public R<String> updata(@RequestBody Category category){
        log.info("修改分类信息{}",category);

        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }

    /**
     * 前端是个tyoe类型 用category实例接受
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件  根据type,写两遍是因为对象
        queryWrapper.eq(category.getType() !=null,Category::getType,category.getType());
        //添加排序条件 优先使用sort排序,相同用creatTime排序
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getCreateTime);
        //转发给serive
        List<Category> list  = categoryService.list(queryWrapper);
        return  R.success(list);

    }

}
