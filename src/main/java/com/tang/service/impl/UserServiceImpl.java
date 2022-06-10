package com.tang.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tang.common.Constants;
import com.tang.controller.dto.UserDto;
import com.tang.entity.Menu;
import com.tang.entity.User;
import com.tang.exception.ServiceException;
import com.tang.mapper.RoleMapper;
import com.tang.mapper.RoleMenuMapper;
import com.tang.mapper.UserMapper;
import com.tang.service.IMenuService;
import com.tang.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tang.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
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
    @Autowired
    private UserMapper userMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private RoleMenuMapper roleMenuMapper;
    @Resource
    private IMenuService menuService;

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
            String token = TokenUtils.genToken(user.getId().toString(), user.getPassword());
            user.setToken(token);

            String role = one.getRole();
            List<Menu> roleMenus = getRoleMenus(role);
            user.setMenus(roleMenus);
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
        insertUser.setRole(user.getRole());
        save(insertUser);
        return true;
    }

    @Override
    public IPage<User> selectDeleteById(IPage<User> page,String username, String email, String address) {
        return userMapper.selectDeleteById(page,username,email,address);
    }

    @Override
    public Boolean saveUpdateBy(User user) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username",user.getUsername());
        if(user.getId()!=null){
            wrapper.ne("id",user.getId());
//            System.err.println(user.getUsername());
//            System.err.println(list(wrapper).size());
            if(list(wrapper).size()==0){
                return updateById(user);
            }else {
                throw new ServiceException(Constants.CODE_400,"用户名已被占用");
            }
        }else{
            if(list(wrapper).size()>0){
                throw new ServiceException(Constants.CODE_400,"用户名已存在");
            }else {
                return save(user);
            }
        }
    }

    @Override
    public Boolean deletedById(Integer id) {
        return userMapper.deletedById(id);
    }

    @Override
    public Boolean updateDeleted(Integer id,String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username",username);
        if(userMapper.selectList(wrapper).size()>0){
            throw new ServiceException(Constants.CODE_600,"用户名被占用无法恢复");
        }
        return userMapper.updateDeleted(id);
    }

    @Override
    public Boolean updateBatch(List<UserDto> userDto) {
        int sum = 0;
        for (int i = 0; i < userDto.size(); i++) {
            String username = userDto.get(i).getUsername();
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("username",username);
            if(userMapper.selectList(wrapper).size()>0){
                sum++;
            }else{
                userMapper.updateDeleted(userDto.get(i).getId());
            }
        }
        if(sum>0){
            throw new ServiceException(Constants.CODE_600,"有"+sum+"条数据恢复失败,用户名冲突!");
        }else {
            return true;
        }
    }

    public List<Menu> getRoleMenus(String role){
        Integer roleId = roleMapper.selectByFlag(role);
        List<Integer> menuIds = roleMenuMapper.selectByRoleId(roleId);
        // 查出系统所有的菜单
        List<Menu> menus = menuService.findMenus("");
        System.err.println(menuIds);
        System.err.println(menus.toString());
        // new 一个最后筛选完成之后的list
        List<Menu> roleMenus = new ArrayList<>();
        // 筛选当前用户角色的完整菜单
        for (Menu menu : menus) {
            if(menuIds.contains(menu.getId())) {
                roleMenus.add(menu);
            }
            System.err.println(roleMenus);
            List<Menu> menuList = menu.getMenuList();
            // 移除 menuList 不在 menuIds 中的元素
            menuList.removeIf(chile -> !menuIds.contains(chile.getId()));
        }
        return roleMenus;
    }

    @Override
    public Boolean empty() {
        if(userMapper.emptyCout()>0){
            userMapper.empty();
            return true;
        }else return false;
    }

    @Override
    public void flagRole(String flagRole,String role) {
        userMapper.flagRole(flagRole,role);
    }

}
