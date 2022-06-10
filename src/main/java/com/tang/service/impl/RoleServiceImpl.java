package com.tang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tang.common.Constants;
import com.tang.entity.Role;
import com.tang.entity.User;
import com.tang.exception.ServiceException;
import com.tang.mapper.RoleMapper;
import com.tang.mapper.UserMapper;
import com.tang.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tang
 * @since 2022-06-05
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {
    @Resource
    private UserMapper userMapper;
    // 删除标识时查询该标识是否在使用中
    @Override
    public Boolean removeByIdOrRole(Integer id){
        String flag = getById(id).getFlag();
        // 根据标识查询是否存在使用该标识的用户
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("role",flag);
        if(userMapper.selectList(wrapper).size()>0){
            throw new ServiceException(Constants.CODE_600,"该标识存在用户使用不能删除");
        }
        removeById(id);
        return true;
    }
}
