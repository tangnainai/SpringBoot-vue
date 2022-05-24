package com.tang.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tang.common.Constants;
import com.tang.controller.dto.UserDto;
import com.tang.entity.User;
import com.tang.exception.ServiceException;
import com.tang.mapper.UserMapper;
import com.tang.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tang
 * @since 2022-05-23
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    private static final Log LOG = Log.get();
    @Override
    public UserDto login(UserDto user) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username",user.getUsername());
        wrapper.eq("password",user.getPassword());
//        List<User> list = list(wrapper);
        User one;
        try {
            one = getOne(wrapper);
        }catch (Exception e){
            LOG.error(e);
            throw new ServiceException(Constants.CODE_500,"系统错误");
        }
        if (one!=null){
            BeanUtil.copyProperties(one,user,true);
            return user;
        }else {
            throw new ServiceException(Constants.CODE_600,"用户名或密码错误");
        }
    }

    @Override
    public Boolean register(UserDto user) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username",user.getUsername());
        List<User> list = list(wrapper);
        if(list.size()>0){
            throw new ServiceException(Constants.CODE_600,"用户名已经存在");
        }
        User insertUser = new User();
        insertUser.setUsername(user.getUsername());
        insertUser.setPassword(user.getPassword());
        save(insertUser);
        return true;
    }
}
