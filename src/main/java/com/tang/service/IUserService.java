package com.tang.service;

import com.tang.controller.dto.UserDto;
import com.tang.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tang
 * @since 2022-05-23
 */
public interface IUserService extends IService<User> {
    // 登录验证
    UserDto login(UserDto user);
    // 注册
    Boolean register(UserDto user);
}
