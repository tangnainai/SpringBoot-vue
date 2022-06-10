package com.tang.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tang.common.Result;
import com.tang.entity.User;
import com.tang.service.IUserService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.tang.service.ICourseService;
import com.tang.entity.Course;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author tang
 * @since 2022-06-10
 */
@RestController
@RequestMapping("/course")
public class CourseController {
    @Resource
    private ICourseService courseService;
    @Resource
    private IUserService userService;

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Course course) {
        return Result.success(courseService.saveOrUpdate(course));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(courseService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(courseService.removeByIds(ids));
    }

    @GetMapping
    public Result findAll() {
        return Result.success(courseService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(courseService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer current,
                           @RequestParam Integer size,
                           @RequestParam String name) {
        return Result.success(courseService.findPage(new Page<>(current, size), name));
    }

    /**
     * 查询授课老师
     */
    @GetMapping("/username/{role}")
    public Result findUserName(@PathVariable String role) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("role",role);
        return Result.success(userService.list(wrapper));
    }

}

