package com.tang.mapper;

import com.tang.entity.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
public interface MenuMapper extends BaseMapper<Menu> {

    @Select("select pid from sys_menu where id=#{id};")
    Integer menuByPid(@Param("id")Integer id);
}
