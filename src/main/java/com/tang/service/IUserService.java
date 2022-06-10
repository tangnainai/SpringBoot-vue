package com.tang.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tang.controller.dto.UserDto;
import com.tang.entity.Menu;
import com.tang.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
    // 查询逻辑删除的数据
    IPage<User> selectDeleteById(IPage<User> page,String username, String email, String address);
    // 修改或插入
    Boolean saveUpdateBy(User user);
    // 永久删除
    Boolean deletedById(Integer id);
    // 修改deleted=0
    Boolean updateDeleted(Integer id,String username);
    // 批量修改deleted=0
    Boolean updateBatch(List<UserDto> list);

    public List<Menu> getRoleMenus(String role);

    Boolean empty();

    void flagRole(String flagRole,String role);
}
