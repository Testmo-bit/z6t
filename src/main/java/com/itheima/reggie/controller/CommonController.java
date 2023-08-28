package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传与下载
 * multipartFile file(必须与前端name对应)
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    //转发保存的路径 用配置文件方式写
    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    //file是一个临时文件,本次请求完成后会删除
    public R<String> upLoad(MultipartFile file) {
        log.info(file.toString());
        //获取原来的文件名
        String originalFilename = file.getOriginalFilename();//a.jpg
        //获取原始文件后缀名,lastIndexOf:从给定的关键字开始截取
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用uuid生成随机文件名,避免文件重复
        String fileName = UUID.randomUUID().toString() + suffix;
        //创建目录对象 ,上传时创建img目录,配置文件里写了
        File dir = new File(basePath);
        //判断当前目录是否存在
        if (!dir.exists()) {
            //目录不存在,需要创建
            dir.mkdir();
        }
        try {
            //转发临时文件永远保存到一个路径 d://改为配置文件
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }
    /**
     * 文件下载
     *
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        try {
            //输入流,通过输入流读取文件内容

            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
//      打错的代码  FileInputStream fileInputStream = new FileInputStream(new FileInputStream(new File(basePath + name)));
            //输出流.通过输出流把文件希尔浏览器,在浏览器展示
            ServletOutputStream outputStream = response.getOutputStream();
            //设置响应的是图片文件
            response.setContentType("image/jpeg");

            int len = 0;
            //定义一个byte数组,放进数组,等于-1时代表读完,退出
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                //写
                outputStream.write(bytes, 0, len);
                //刷新写入的数据,也就是从临时文件到保存
                outputStream.flush();
            }

            //关闭资源
            outputStream.close();
            fileInputStream.close();
            //Exception大异常
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
