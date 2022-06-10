package com.tang.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tang.entity.Course;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author tang
 * @since 2022-06-10
 */
public interface CourseMapper extends BaseMapper<Course> {

    IPage<Course> findPage(Page<Course> page, String name);

    @Select("select * from sys_course where teacher_id = #{id} ;")
    List<Course> courseList(@Param("id") Integer id);
}
