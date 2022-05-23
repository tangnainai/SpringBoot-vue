package com.tang.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tang.entity.User;
import com.tang.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
