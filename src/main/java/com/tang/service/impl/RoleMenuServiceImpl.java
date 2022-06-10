package com.tang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tang.entity.Menu;
import com.tang.entity.RoleMenu;
import com.tang.mapper.MenuMapper;
import com.tang.mapper.RoleMenuMapper;
import com.tang.service.IRoleMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tang
 * @since 2022-06-06
 */
@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements IRoleMenuService {
    @Resource
    private RoleMenuMapper roleMenuMapper;
    @Resource
    private MenuMapper menuMapper;

    @Override
    public int updateIsHaveBatch(Integer id) {
        return roleMenuMapper.updateIsHaveBatch(id);
    }

    @Override
    public List<Integer> listMenu(Integer roleId) {
        List<Integer> menuIds = roleMenuMapper.listMenu(roleId);
        for (int i = 0; i < menuIds.size(); i++) {
            QueryWrapper<Menu> wrapper = new QueryWrapper<Menu>().eq("pid", menuIds.get(i));
            if(menuMapper.selectList(wrapper).size()>0){
                menuIds.remove(i);
            }
        }
        return menuIds;
    }
}
