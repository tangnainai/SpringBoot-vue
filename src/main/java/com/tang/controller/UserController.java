package com.tang.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.intern.InternUtil;
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
import com.tang.exception.ServiceException;
import com.tang.service.ICourseService;
import com.tang.service.IUserService;
import com.tang.utils.TokenUtils;
import com.tang.utils.UserEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
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
    @Resource
    private ICourseService courseService;

    /**
     * 批量恢复
     * @param userDto
     * @return
     */
    @PostMapping("/updateBatch")
    public Result updateBatch(@RequestBody List<UserDto> userDto){
        return Result.success(IUserService.updateBatch(userDto));
    }

    /**
     * 批量永久删除
     * @param ids
     * @return
     */
    @PostMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        for (Integer id : ids) {
            IUserService.deletedById(id);
        }
        return Result.success();
    }

    /**
     * 恢复
     * @param user
     * @return
     */
    @PostMapping("/updateDeleted")
    public Result updateDeleted(@RequestBody User user){
        return Result.success(IUserService.updateDeleted(user.getId(),user.getUsername()));
    }

    /**
     * 永久删除
     * @param id
     * @return
     */
    @PostMapping("/deletedById")
    public Result deletedById(@RequestBody Integer id){
        return Result.success(IUserService.deletedById(id));
    }

    /**
     * 回收站查询
     * @param current
     * @param size
     * @param username
     * @param email
     * @param address
     * @return
     */
    @GetMapping("/deletePage")
    public Result deletePage(@RequestParam Integer current,
                             @RequestParam Integer size,
                             @RequestParam(defaultValue = "") String username,
                             @RequestParam(defaultValue = "") String email,
                             @RequestParam(defaultValue = "") String address){
        IPage<User> page = new Page<>(current, size);
        return Result.success(IUserService.selectDeleteById(page,username,email,address));
    }

    /**
     * 根据用户id查询 课程
     * @param id
     * @return
     */
    @GetMapping("/course/{id}")
    public Result findPageCourse(@PathVariable Integer id){
        return Result.success(courseService.courseList(id));
    }

    /**
     * 用户查询
     * @param current
     * @param size
     * @param username
     * @param email
     * @param address
     * @return
     */
    @GetMapping("/page")
    public Result page(@RequestParam Integer current,
                            @RequestParam Integer size,
                            @RequestParam(defaultValue = "") String username,
                            @RequestParam(defaultValue = "") String email,
                            @RequestParam(defaultValue = "") String address,
                            @RequestParam Integer id,
                            @RequestParam String role){
        IPage<User> page = new Page<>(current,size);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if(!"".equals(username))
            wrapper.like("username",username);
        if(!"".equals(email))
            wrapper.like("email",email);
        if(!"".equals(address))
            wrapper.like("address",address);
        wrapper.orderByDesc("id");
        wrapper.ne("id",id); // 不查询当前用户数据
        if(role.equals(UserEnum.ROLE_STUDENT.name())||role.equals(UserEnum.ROLE_TEACHER.name())){
            wrapper.ne("role","ROLE_ADMIN");
        }
        page = IUserService.page(page,wrapper);

        return Result.success(page);
    }

    /**
     * 插入或修改用户
     * @param user
     * @return
     */
    @PostMapping("/save")
    public Result save(@RequestBody User user){
        if(StrUtil.isBlank(user.getRole())){
            user.setRole(UserEnum.ROLE_STUDENT.name());
        }
        return Result.success(IUserService.saveUpdateBy(user));
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @PostMapping("/open")
    // post方法要用RequestBody 求情数据
    public Result delete(@RequestBody Integer id){
        User currentUser = TokenUtils.getCurrentUser();
        if(currentUser.getId()==id){
            throw new ServiceException(Constants.CODE_600,"当前登录用户不能删除");
        }else{
            IUserService.removeById(id);
        }
        return Result.success(IUserService.removeById(id));
    }

    /**
     * 批量删除
     * @param id
     * @return
     */
    @PostMapping("/openMap")
    public Result deleteMap(@RequestBody List<Integer> id){
        return Result.success(IUserService.removeBatchByIds(id));
    }

    /**
     * 导出
     * @param response
     * @param role
     * @throws Exception
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response,
                       @RequestParam String role) throws Exception{
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        // 判断是用户还是管理员导出数据
        if(UserEnum.ROLE_STUDENT.name().equals(role)){
            wrapper.eq("role",role);
        }
        // 从数据库查询出所有的数据
        List<User> list = IUserService.list(wrapper);
        // 通过工具类创建writer 写出到磁盘路径
//        ExcelWriter writer = ExcelUtil.getWriter(filesUploadPath + "/用户信息.xlsx");
        // 在内存操作，写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);
        //自定义标题别名
        writer.addHeaderAlias("username", "用户名");
        writer.addHeaderAlias("password", "密码");
        writer.addHeaderAlias("role", "角色");
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
    public  Result imp(MultipartFile file) throws Exception{
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        // 方式1：(推荐) 通过 javabean的方式读取Excel内的对象，但是要求表头必须是英文，跟javabean的属性要对应起来
//        List<User> list = reader.readAll(User.class);
        // 方式2：忽略表头的中文，直接读取表的内容
        List<List<Object>> list = reader.read(1);
        List<User> users = CollUtil.newArrayList();
        int sum = 0; // 记录插入了多少数据
        for (List<Object> row : list) {
            User user = new User();
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("username",row.get(0).toString());
            if(IUserService.list(wrapper).size()>0){
                sum++;
                continue;
            }
            user.setUsername(row.get(0).toString());
            user.setPassword(row.get(1).toString());
            user.setNickname(row.get(2).toString());
            user.setEmail(row.get(3).toString());
            user.setPhone(row.get(4).toString());
            user.setAddress(row.get(5).toString());
            user.setAvatarUrl(row.get(6).toString());
            users.add(user);
            IUserService.save(user);
        }
        if(sum>0){
            throw new ServiceException(Constants.CODE_400,list.size()-sum+"条数据插入成功,有"+sum+"条数据用户名冲突");
        }
        return Result.success();
    }

    /**
     *  获取个人信息
     * @param username
     * @return
     */
    @GetMapping("/username/{username}")
    public Result getUsername(@PathVariable String username){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username",username);
        return Result.success(IUserService.getOne(wrapper));
    }

    /**
     * 清空回收站
     * @return
     */
    @PostMapping("/empty")
    public Result empty(){
        return Result.success(IUserService.empty());
    }

    /**
     * 修改密码
     * @return
     */
    @PostMapping("/modify")
    public Result modify(@RequestBody User user){
        return Result.success(IUserService.updateById(user));
    }

}
