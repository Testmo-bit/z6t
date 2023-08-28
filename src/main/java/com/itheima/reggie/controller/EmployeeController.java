package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
//浏览器请求url例:请求 URL:
//http://localhost:8080/employee/page?page=1&pageSize=2&name=ass
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3、如果没有查询到则返回登录失败结果
        if (emp == null) {
            return R.error("登录失败");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }

        //6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee", emp.getId());
        log.info("登录成功");
        return R.success(emp);
    }

    /**
     * 员工退出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清理Session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 添加员工
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工,员工信息:{}", employee.toString());

        //设置初始密码123456,md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        //获得当前登录用户的id
//        Long empId = (long) request.getSession().getAttribute(("employee"));//登录时session已经保存当前用户id,61行
//
//        employee.setCreateUser(empId); //更新人和创建人
//        employee.setUpdateUser(empId);
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * 分页查询
     * <page></>mybat提供返回分页查询的结果,<page>有分页查询信息,总记录数,当前页码等
     * R<Page> 的含义是带有分页信息的响应对象，其中的 <Page> 表示分页对象，包含了分页查询的相关信息，如总记录数、当前页码、每页记录数等。
     *
     * 通过使用 R<Page>，可以方便地获取分页查询的结果数据，并获取分页相关的信息。它提供了一些方法，如 getRecords() 用于获取查询结果的列表，getTotal() 用于获取总记录数，getCurrent() 用于获取当前页码，getSize() 用于获取每页记录数等。
     * @param page
     * @param name
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);

        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();

        //添加过滤条件  like有两个方法,boolean 对象 如果前值为false,则不查询
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo,queryWrapper); //page
        return R.success(pageInfo);
    }
    /**
     *根据id修改员工
     * 会报错,浏览器只能处理json前16位数据,丢失精度了{
     *   "id": 1693673179195220000,
     * 数据库:  1693673179195219969
     *   "status": 0
     *   解决思路:转为string类型,扩展消息转换器到java到json对象
     * }
     * @param  employee
     * @return
     */
    @PutMapping //上面已经有了employee这里只写putmapping就可以
    public R<String> upData(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());

//        Long empId = (Long)request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        System.out.println("撒是"+empId);
//        empidloyee.setUpdateUser(empId); 到公共字段拦截器拦截
        long id = Thread.currentThread().getId();
        log.info("线程id为: {}",id);
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }
    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息...");
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }
}
