package com.tang.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tang.common.Constants;
import com.tang.common.Result;
import com.tang.entity.FileUrl;
import com.tang.exception.ServiceException;
import com.tang.mapper.FileMapper;
import com.tang.service.IFileService;
import com.tang.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.*;

/**
 * 文件上传相关接口
 * @author tang
 * @date 2022/5/30 20:59
 * @desc
 */
@RestController
@RequestMapping("/file")
public class FileController {
    @Resource
    private IFileService fileService;

    @Value("${files.upload.path}")
    private String fileUploadPath;
    @Value("${files.serverIp}")
    private String serverIp;

    private static String deleteTime = TimeUtils.format(System.currentTimeMillis(),"yy-MM-dd HH:mm:ss");

    /**
     * 上传文件
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/upload")
    public String upload(@RequestBody MultipartFile file) throws IOException{
        String url = uploadFile(file);
        return url;
    }

    /**
     * 文件上传方法
     * @param file
     * @return
     * @throws IOException
     */
    public String uploadFile(MultipartFile file) throws IOException {
        FileUrl saveFile = new FileUrl();

        String originalFilename = file.getOriginalFilename();
        String type = FileUtil.extName(originalFilename);
        long size = file.getSize();

        // 定义一个文件唯一的标识码
        String uuid = IdUtil.fastSimpleUUID();
        String fileUUID = uuid + StrUtil.DOT + type;

        File uploadFile = new File(fileUploadPath + fileUUID);
        // 判断配置的文件目录是否存在，若不存在则创建一个新的文件目录
        File parentFile = uploadFile.getParentFile();
        if(!parentFile.exists()) {
            parentFile.mkdirs();
        }
        String url;
        // 获取文件的md5
        String md5 = SecureUtil.md5(file.getInputStream());
        // 从数据库查询是否存在相同的记录
        FileUrl dbFiles = getFileByMd5(md5);
        if (dbFiles != null) { // 文件已存在
            url = dbFiles.getUrl();
            saveFile.setFileName(dbFiles.getFileName());
        } else {
            // 上传文件到磁盘
            file.transferTo(uploadFile);
            // 数据库若不存在重复文件，则不删除刚才上传的文件
            url = "http://"+serverIp+":8088/file/" + fileUUID;
//            url = "http://120.24.229.94:8088/file/" + fileUUID;
            saveFile.setFileName(fileUUID);
        }
        // 存储数据库.;
        saveFile.setName(originalFilename);
        saveFile.setType(type);
        saveFile.setSize(size/1024);
        saveFile.setUrl(url);
        saveFile.setMd5(md5);
        fileService.save(saveFile);
        return url;
    }
    /**
     * 通过文件的md5查询文件
     * @param md5
     * @return
     */
    private FileUrl getFileByMd5(String md5) {
        // 查询文件的md5是否存在
        List<FileUrl> fileList = fileService.selectMd5(md5);
        return fileList.size() == 0 ? null : fileList.get(0);
    }

    /**
     * 下载文件
     * @param fileUuid
     * @param resp
     * @throws IOException
     */
    @GetMapping("/{fileUuid}")
    public void download(@PathVariable String fileUuid,HttpServletResponse resp) throws IOException {
        // 根据文件的唯一标识获取文件
        File uploadFile = new File(fileUploadPath + fileUuid);
        // 设置输出流的格式
        ServletOutputStream os = resp.getOutputStream();
        resp.addHeader("Content-Disposition","attachment;filename="+ URLEncoder.encode(fileUuid,"utf-8"));
        resp.setContentType("application/octet-stream");

        // 读取文字的字节流
        os.write(FileUtil.readBytes(uploadFile));
        os.flush();
        os.close();
    }

    /**
     * 分页查询文件
     * @param current
     * @param sizePage
     * @param name
     * @return
     */
    @GetMapping("/query")
    public Result query(@RequestParam Integer current,
                        @RequestParam Integer sizePage,
                        @RequestParam(defaultValue="")String name){
        IPage<FileUrl> page = new Page<>(current, sizePage);
        QueryWrapper<FileUrl> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        if(!"".equals(name)){
            wrapper.like("name",name);
        }
        page = fileService.page(page,wrapper);
        return Result.success(page);
    }

    /**
     * 是否禁用
     * @param fileUrl
     * @return
     */
    @PostMapping("/updateEnable")
    public Result updateEnable(@RequestBody FileUrl fileUrl){
        return Result.success(fileService.updateById(fileUrl));
    }

    /**
     * 逻辑删除文件
     * @param id
     * @return
     */
    @PostMapping("/removeById")
    public Result deleteById(@RequestBody Integer id){
        FileUrl file = new FileUrl();
        file.setDeleteTime(deleteTime);
        file.setId(id);
        fileService.updateById(file);
        return Result.success(fileService.removeById(id));
    }

    /**
     * 逻辑批量删除
     * @param ids
     * @return
     */
    @PostMapping("/delBatch")
    public Result delBatch(@RequestBody List<Integer> ids){
        FileUrl file = new FileUrl();
        for (Integer id : ids) {
            file.setId(id);
            file.setDeleteTime(deleteTime);
            fileService.updateById(file);
        }
        return Result.success(fileService.removeBatchByIds(ids));
    }

    /**
     * 查询逻辑删除了的
     * @param name
     * @param current
     * @param sizePage
     * @return
     */
    @GetMapping("/queryBatch")
    public Result queryBatch(@RequestParam(defaultValue = "") String name,
                           @RequestParam Integer current,
                           @RequestParam Integer sizePage) throws ParseException {
        IPage<FileUrl> page = new Page<>(current, sizePage);
        return Result.success(fileService.queryBatch(page,name));
    }

    /**
     * 恢复文件
     * @param id
     * @return
     */
    @PostMapping("/updateIsDelete")
    public Result updateIsDelete(@RequestBody Integer id){
        return Result.success(fileService.updateIsDelete(id));
    }

    /**
     * 批量恢复文件
     * @param ids
     * @return
     */
    @PostMapping("/updateBatch")
    public Result updateBatch(@RequestBody List<Integer> ids){
        for (Integer id : ids) {
            fileService.updateIsDelete(id);
        }
        return Result.success();
    }

    /**
     * 彻底删除文件
     * @param id
     * @return
     */
    @PostMapping("/thoroughlyDelete")
    public Result thoroughlyDelete(@RequestBody Integer id){
        return Result.success(fileService.thoroughlyDelete(id)); // 彻底删除文件
    }

    /**
     * 彻底批量删除文件
     * @param ids
     * @return
     */
    @PostMapping("/thoroughlyBatch")
    public Result thoroughlyBatch(@RequestBody List<Integer> ids){
        for (Integer id : ids) {
            fileService.thoroughlyDelete(id);
        }
        return Result.success();
    }

    /**
     * 清空回收站
     * @return
     */
    @PostMapping("/empty")
    public Result empty(){
        return Result.success(fileService.empty());
    }

}
