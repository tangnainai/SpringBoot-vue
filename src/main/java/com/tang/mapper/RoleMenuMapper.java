package com.tang.mapper;

import com.tang.entity.RoleMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author tang
 * @since 2022-06-06
 */
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
    @Update("update role_menu set is_have = 0 where role_id=#{id};")
    int updateIsHaveBatch(@Param("id")Integer id);

    @Select("select menu_id from role_menu where role_id=#{roleId} ;")
    List<Integer> selectByRoleId(@Param("roleId") Integer roleId);

    @Select("SELECT menu_id from sys_menu,role_menu where menu_id=id and role_id=#{roleId}")
    List<Integer> listMenu(@Param("roleId")Integer roleId);
}
