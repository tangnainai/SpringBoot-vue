package com.tang.controller.dto;

import lombok.Data;

import java.util.List;

/**
 * @author tang
 * @date 2022/6/6 15:14
 * @desc
 */
@Data
public class RoleMenuDto {
    private Integer roleId;
    private List<Integer> menuId;
}
