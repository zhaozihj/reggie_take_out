package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.basePath}")
    //从application.yml文件中获取这个路径
    /*
    reggie:
      basePath: D:\img1\
     */
    private String basePath;


    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")

    public R<String> upload(@RequestParam("file") MultipartFile file) {
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        log.info(file.toString());

        //原始文件名
        String originalFilename = file.getOriginalFilename();
        //获取文件的后缀名
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID重新生成文件名，防止文件名称重复造成文件覆盖
        //uuid是一个随机生成不重复文件名的工具
        String filename = UUID.randomUUID().toString() + suffix;

        //创建一个目录对象
        File dir = new File(basePath);
        //判断当前目录是否存在，如果不存在就创建这个目录

        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            //调用这个方法的时候除了文件可以不存在，路径上的文件夹都一定要存在
            file.transferTo(new File(basePath + filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(filename);
    }

    @GetMapping("/download")
    public void download(String name , HttpServletResponse response) {
        try {
//输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

            //输出流，通过输出流将文件写回浏览器，在浏览器展示图片了
            ServletOutputStream outputStream = response.getOutputStream();

            //设置响应回去的文件时图片类型
            response.setContentType("image/jpeg");

            byte[] bytes = new byte[1024];
            int len = 0;
            //把文件通过输入流输入到byte数组
            //两个流之间进行配合，输入流把文件输入到内存中，然后输出流再把文件协会到浏览器
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }

            //关闭资源
            outputStream.close();
            fileInputStream.close();

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}

