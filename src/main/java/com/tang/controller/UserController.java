package com.tang.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tang.common.Constants;
import com.tang.common.Result;
import com.tang.controller.dto.UserDto;
import com.tang.entity.User;
import com.tang.service.IUserService;
import com.tang.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author tang
 * @date 2022/5/22 15:50
 * @desc
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService IUserService;
    @GetMapping("/page")
    public IPage<User> page(@RequestParam Integer current,
                            @RequestParam Integer size,
                            @RequestParam(defaultValue = "") String username,
                            @RequestParam(defaultValue = "") String email,
                            @RequestParam(defaultValue = "") String address){
        IPage<User> page = new Page<>(current,size);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if(!"".equals(username))
            wrapper.like("username",username);
        if(!"".equals(email))
            wrapper.like("email",email);
        if(!"".equals(address))
            wrapper.like("address",address);
        wrapper.orderByDesc("id");
        page = IUserService.page(page,wrapper);
        return page;
    }
    @PostMapping("/save")
    public Boolean save(@RequestBody User user){
        return IUserService.saveOrUpdate(user);
    }

    @PostMapping("/open")
    // post方法要用RequestBody 求情数据
    public Boolean delete(@RequestBody Integer id){
        return IUserService.removeById(id);
    }
    @PostMapping("/openMap")
    public Boolean deleteMap(@RequestBody List<Integer> id){
        return IUserService.removeBatchByIds(id);
    }

    /**
     * 导出
     * @param response
     * @throws Exception
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception{
        // 从数据库查询出所有的数据
        List<User> list = IUserService.list();
        // 通过工具类创建writer 写出到磁盘路径
//        ExcelWriter writer = ExcelUtil.getWriter(filesUploadPath + "/用户信息.xlsx");
        // 在内存操作，写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);
        //自定义标题别名
        writer.addHeaderAlias("username", "用户名");
        writer.addHeaderAlias("password", "密码");
        writer.addHeaderAlias("nickname", "昵称");
        writer.addHeaderAlias("email", "邮箱");
        writer.addHeaderAlias("phone", "电话");
        writer.addHeaderAlias("address", "地址");
        writer.addHeaderAlias("createTime", "创建时间");
        writer.addHeaderAlias("avatarUrl", "头像");

        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list, true);

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("用户信息", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();
    }

    /**
     * 导入
     * @param file
     * @throws Exception
     */
    @PostMapping("/import")
    public Boolean imp(MultipartFile file) throws Exception{
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        // 方式1：(推荐) 通过 javabean的方式读取Excel内的对象，但是要求表头必须是英文，跟javabean的属性要对应起来
//        List<User> list = reader.readAll(User.class);
        // 方式2：忽略表头的中文，直接读取表的内容
        List<List<Object>> list = reader.read(1);
        List<User> users = CollUtil.newArrayList();
        for (List<Object> row : list) {
            User user = new User();
            user.setUsername(row.get(0).toString());
            user.setPassword(row.get(1).toString());
            user.setNickname(row.get(2).toString());
            user.setEmail(row.get(3).toString());
            user.setPhone(row.get(4).toString());
            user.setAddress(row.get(5).toString());
            user.setAvatarUrl(row.get(6).toString());
            users.add(user);
        }

        IUserService.saveBatch(users);
        return true;
    }

    @PostMapping("/login")
    public Result login(@RequestBody UserDto user){
        String username = user.getUsername();
        String password = user.getPassword();
        if(StrUtil.isBlank(username)||StrUtil.isBlank(password)){
            return Result.error(Constants.CODE_400,"参数错误");
        }
        UserDto dto = IUserService.login(user);
        return Result.success(dto);
    }

    @PostMapping("/register")
    public Result register(@RequestBody UserDto userDto){
        String username = userDto.getUsername();
        String password = userDto.getPassword();
        if(StrUtil.isBlank(username)||StrUtil.isBlank(password)){
            return Result.error(Constants.CODE_400,"参数错误");
        }
        IUserService.register(userDto);
        return Result.success();
    }
}
