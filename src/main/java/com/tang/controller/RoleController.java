package com.tang.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.org.apache.bcel.internal.generic.FieldOrMethod;
import com.tang.common.Constants;
import com.tang.common.Result;
import com.tang.controller.dto.RoleMenuDto;
import com.tang.entity.RoleMenu;
import com.tang.entity.User;
import com.tang.exception.ServiceException;
import com.tang.mapper.MenuMapper;
import com.tang.service.IMenuService;
import com.tang.service.IRoleMenuService;
import com.tang.service.IUserService;
import com.tang.utils.TimeUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.tang.service.IRoleService;
import com.tang.entity.Role;

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
@RequestMapping("/role")
public class RoleController {

    @Resource
    private IRoleService roleService;

    @Resource
    private IRoleMenuService roleMenuService;

    @Resource
    private IUserService userService;

    @Resource
    private MenuMapper menuMapper;

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Role role) {
        role.setTime(TimeUtils.format(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss"));
        // 判断标识唯一
        String flag = role.getFlag();
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        wrapper.eq("flag",flag);
        if (role.getId()==null){
            if(roleService.list(wrapper).size()>0){
                throw new ServiceException(Constants.CODE_500,"标识已存在");
            }else {
                // 判断名称唯一
                QueryWrapper<Role> roleName = new QueryWrapper<>();
                roleName.eq("name",role.getName());
                if(roleService.list(roleName).size()>0){
                    throw new ServiceException(Constants.CODE_500,"名称已存在");
                }else{
                    roleService.save(role);
                }
            }
        }else{
            String flagRole = role.getFlagRole();
            userService.flagRole(flagRole,role.getFlag()); // 修改用户的标识
            roleService.update(role,new QueryWrapper<Role>().eq("id",role.getId()));
        }
        return Result.success();
    }

    // 根据id删除
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(roleService.removeByIdOrRole(id));
    }

    // 批量删除
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(roleService.removeByIds(ids));
    }

    // 分页查询
    @GetMapping
    public Result findAll(@RequestParam Integer current,
                          @RequestParam Integer size,
                          @RequestParam(defaultValue = "") String name,
                          @RequestParam(defaultValue = "") String description) {
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        if(!"".equals(name))
        wrapper.like("name",name);
        if(!"".equals(description))
        wrapper.like("description",description);
        IPage<Role> page = new Page<>(current,size);
        page = roleService.page(page,wrapper);
        return Result.success(page);
    }
    // 获取全部数据
    @GetMapping("/list")
    public Result list(){
        return Result.success(roleService.list());
    }
    // 根据id查询
    @GetMapping("/{id}")
    public Role findOne(@PathVariable Integer id) {
        return roleService.getById(id);
    }

    @PostMapping("/roleMenu")
    public Result saveRoleMenu(@RequestBody RoleMenuDto roleMenu) {
        Integer roleId = roleMenu.getRoleId();
        List<Integer> menuId = roleMenu.getMenuId();

        // 判断是否存在父级
        HashSet<Integer> hash = new HashSet<>();
        for (Integer id : menuId) {
            Integer pid = menuMapper.menuByPid(id);
            if(pid!=null){
                hash.add(pid);
            }
            hash.add(id);
        }

        QueryWrapper<RoleMenu> wrapper = new QueryWrapper<>();
        wrapper.eq("role_id",roleId);
        roleMenuService.remove(wrapper); // 修改
        // 根据 roleId 和 menuId 插入或修改是否存在权限
        for (Integer id : hash) {
            RoleMenu role = new RoleMenu();
            role.setRoleId(roleId);
            role.setMenuId(id);
            roleMenuService.save(role);
        }
        return Result.success();
    }

    @GetMapping("/roleMenu")
    public Result roleIdList(@RequestParam Integer roleId) {
        return Result.success(roleMenuService.listMenu(roleId));
    }
}

