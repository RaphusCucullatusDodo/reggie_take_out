package com.raphus.reggie.controller;

import com.raphus.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传和下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.imagePath}")
    private String imagePath;

    /**
     * 文件上传
     *
     * @param file 上传文件
     * @return 文件名
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        log.info(file.toString());

        if (file.isEmpty()) {
            return R.error("文件不能为空");
        }

        //原始文件名
        String originalFilename = file.getOriginalFilename();//abc.jpg
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));//获取文件格式

        //使用UUID重新生成文件名，防止文件名称重复造成文件覆盖
        String fileName = UUID.randomUUID() + suffix;//dfsdfdfd.jpg

        //创建一个目录对象
        File dir = new File(imagePath);
        //判断当前目录是否存在
        if(!dir.exists()){
            //目录不存在，需要创建
            dir.mkdirs();
        }

        try {
            file.transferTo(new File(imagePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    /**
     * 文件下载(通过文件名下载图片)
     * @param name
     * @param response 输出流用于响应浏览器
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        FileInputStream fileInputStream = null;
        ServletOutputStream outputStream = null;
        try {
            //输入流，通过输入流读取文件内容
            fileInputStream = new FileInputStream(new File(imagePath + name));
            //输出流，通过输出流将文件写回浏览器
            outputStream = response.getOutputStream();

            //设置响应内容类型 (在浏览器中显示)
            response.setContentType("image/jpeg");
//            response.setHeader("content-Disposition", "attachment;filename=" + name); // 用户使用浏览器下载至本地

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //关闭资源
            if (outputStream!=null||fileInputStream!=null){
                try {
                    outputStream.close();
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
