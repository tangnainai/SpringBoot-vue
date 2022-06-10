package com.tang.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tang.common.Constants;
import com.tang.common.Result;
import com.tang.controller.dto.UserDto;
import com.tang.entity.Icon;
import com.tang.entity.Role;
import com.tang.entity.RoleMenu;
import com.tang.service.*;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.tang.entity.Menu;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author tang
 * @since 2022-06-05
 */
@RestController
@RequestMapping("/menu")
public class MenuController {
    @Resource
    private IMenuService menuService;
    @Resource
    private IIconService iconService;
    @Resource
    private IUserService userService;
    @Resource
    private IRoleService roleService;
    @Resource
    private IRoleMenuService roleMenuService;

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Menu menu) {
        if(menu.getId()==null){
            menuService.save(menu);
            QueryWrapper<Role> wrapper = new QueryWrapper<Role>().eq("flag", "ROLE_ADMIN");
            Integer id = roleService.getOne(wrapper).getId();
            RoleMenu roleMenu = new RoleMenu(id,menu.getId());
            roleMenuService.save(roleMenu);
        }else{
            menuService.updateById(menu);
        }
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(menuService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(menuService.removeByIds(ids));
    }

    @GetMapping
    public Result findAll() {
        return Result.success(menuService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(menuService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String name) {
        List<Menu> parentNodes = menuService.findMenus(name);
        return Result.success(parentNodes);
    }
    @GetMapping("/icons")
    public Result icons(){
        QueryWrapper<Icon> wrapper = new QueryWrapper<>();
        wrapper.eq("type", Constants.CONSTANT_ICON);
        return  Result.success(iconService.list(wrapper));
    }

    // 修改图标后从新 传递 menus
    @PostMapping("/menus")
    public Result menus(@RequestBody Integer id){
        String role = userService.getById(id).getRole();
        List<Menu> roleMenus = userService.getRoleMenus(role);
        UserDto userDto = new UserDto();
        userDto.setMenus(roleMenus);
        return Result.success(userDto);
    }
}

