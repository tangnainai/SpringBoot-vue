package com.tang.service;

import com.tang.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tang
 * @since 2022-06-05
 */
public interface IRoleService extends IService<Role> {

    Boolean removeByIdOrRole(Integer id);
}
