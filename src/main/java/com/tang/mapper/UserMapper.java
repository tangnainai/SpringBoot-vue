package com.tang.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tang.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author tang
 * @since 2022-05-23
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 查询逻辑删除的数据
    @Select("select * from sys_user " +
            "where deleted=1 AND " +
            "(username LIKE concat('%',#{username},'%')" +
            " AND email LIKE concat('%',#{email},'%')" +
            " AND address LIKE concat('%',#{address},'%'))" +
            " order by id desc")
    IPage<User> selectDeleteById(IPage<User> page, @Param("username")String username,
                          @Param("email")String email,
                          @Param("address")String address);
    // 彻底删除
    @Delete("delete from sys_user where deleted=1 and id=#{id};")
    Boolean deletedById(@Param("id")Integer id);

    // 修改deletedId == 0
    @Update("update sys_user set deleted=0 where id=#{id};")
    Boolean updateDeleted(@Param("id")Integer id);

    @Delete("delete from sys_user where deleted=1;")
    void empty();
    @Select("select count(*) from sys_user where deleted=1")
    Integer emptyCout();

    @Update("update sys_user set role = #{role} where role = #{flagRole} ;")
    void flagRole(String flagRole,String role);
}
