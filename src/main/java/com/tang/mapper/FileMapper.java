package com.tang.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tang.entity.FileUrl;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
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
 * @since 2022-05-30
 */
public interface FileMapper extends BaseMapper<FileUrl> {
    @Select("select * from sys_file where file_name=#{fileName}")
    List<FileUrl> selectFile(@Param("fileName")String fileName);

    @Select("select * from sys_file where md5=#{md5}")
    List<FileUrl> selectMd5(@Param("md5")String md5);

    @Select("select * from sys_file where id=#{id};")
    FileUrl selectFileById(@Param("id")Integer id);

    @Select("select * from sys_file where is_delete=1 and(name like concat('%',#{name},'%'))")
    IPage<FileUrl> queryBatch(IPage<FileUrl> page, @Param("name")String name);

    @Update("update sys_file set is_delete = 0 where id=#{id}")
    Boolean updateIsDelete(@Param("id")Integer id);

    @Delete("delete from sys_file where id=#{id}")
    Boolean thoroughlyDelete(@Param("id")Integer id);

    @Select("select id from sys_file where is_delete=1 ;")
    List<Integer> emptyCount();
}
