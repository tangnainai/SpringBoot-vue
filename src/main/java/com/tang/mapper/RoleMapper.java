package com.tang.mapper;

import com.tang.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author tang
 * @since 2022-06-05
 */
public interface RoleMapper extends BaseMapper<Role> {
    @Select("select * from sys_role where flag=#{flag} ;")
    Integer selectByFlag(@Param("flag") String flag);
}
