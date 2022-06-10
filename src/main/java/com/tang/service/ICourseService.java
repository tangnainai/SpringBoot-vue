package com.tang.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tang.entity.Course;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tang
 * @since 2022-06-10
 */
public interface ICourseService extends IService<Course> {

    IPage<Course> findPage(Page<Course> page, String name);

    List<Course> courseList(Integer id);
}
