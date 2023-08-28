package com.itheima.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@ServletComponentScan  //扫描servle dofilet等组件
@EnableTransactionManagement //开启事务
//扫描@ServletComponentScan 注解启用了在 MyApp 类所在的包及其子包中进行 Servlet 组件扫描。任何使用 @WebServlet、@WebFilter 或 @WebListener 注解的 Servlet、过滤器或监听器都会被自动注册到 Servlet 容器中。
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class,args);
        log.info("项目启动成功...");
    }
}
