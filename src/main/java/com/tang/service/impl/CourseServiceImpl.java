package com.tang.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tang.entity.Course;
import com.tang.mapper.CourseMapper;
import com.tang.service.ICourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tang
 * @since 2022-06-10
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {
    @Resource
    private CourseMapper courseMapper;
    @Override
    public IPage<Course> findPage(Page<Course> page, String name) {
        return courseMapper.findPage(page,name);
    }

    @Override
    public List<Course> courseList(Integer id) {
        return courseMapper.courseList(id);
    }
}
