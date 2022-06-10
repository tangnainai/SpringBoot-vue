package com.tang.controller;

import cn.hutool.core.util.StrUtil;
import com.tang.common.Constants;
import com.tang.common.Result;
import com.tang.controller.dto.UserDto;
import com.tang.service.IUserService;
import com.tang.utils.UserEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tang
 * @date 2022/5/30 16:13
 * @desc
 */
@RestController
public class LoginController {
    @Autowired
    private IUserService userService;
    /**
     * 登录验证
     * @param user
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody UserDto user){
        String username = user.getUsername();
        String password = user.getPassword();
        if(StrUtil.isBlank(username)||StrUtil.isBlank(password)){
            return Result.error(Constants.CODE_400,"参数错误");
        }
        UserDto dto = userService.login(user);
        return Result.success(dto);
    }

    /**
     * 注册
     * @param userDto
     * @return
     */
    @PostMapping("/register")
    public Result register(@RequestBody UserDto userDto){
        String username = userDto.getUsername();
        String password = userDto.getPassword();
        if(StrUtil.isBlank(username)||StrUtil.isBlank(password)){
            return Result.error(Constants.CODE_400,"参数错误");
        }
        userDto.setRole(UserEnum.ROLE_STUDENT.name());
        userService.register(userDto);
        return Result.success();
    }
}
