package com.tang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tang.entity.User;
import com.tang.mapper.UserMapper;
import com.tang.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author tang
 * @date 2022/5/22 15:37
 * @desc
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
