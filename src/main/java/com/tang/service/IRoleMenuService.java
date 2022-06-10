package com.tang.service;

import com.tang.entity.RoleMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tang
 * @since 2022-06-06
 */
public interface IRoleMenuService extends IService<RoleMenu> {
    int updateIsHaveBatch(Integer id);

    List<Integer> listMenu(Integer roleId);
}
