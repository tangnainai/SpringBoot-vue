package com.tang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tang.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author tang
 * @date 2022/5/22 15:36
 * @desc
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
