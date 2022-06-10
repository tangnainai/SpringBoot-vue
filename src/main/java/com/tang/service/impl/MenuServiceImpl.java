package com.tang.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tang.entity.Menu;
import com.tang.mapper.MenuMapper;
import com.tang.service.IMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tang
 * @since 2022-06-05
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

    @Override
    public List<Menu> findMenus(String name) {
        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        if(StrUtil.isNotBlank(name))
        wrapper.like("name",name);
        List<Menu> list = list(wrapper);
        // 找出pid为null的一级菜单
        List<Menu> parentNode = list.stream().filter(menu -> menu.getPid()==null).collect(Collectors.toList());
        // 找出所有一级子菜单
        for (Menu menu : parentNode) {
            // 筛选所有数据中pid=父级id的数据就是二级菜单
            menu.setMenuList(list.stream().filter(m -> menu.getId().equals(m.getPid())).collect(Collectors.toList()));
        }
        return parentNode;
    }
}
