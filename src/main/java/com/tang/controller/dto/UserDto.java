package com.tang.controller.dto;

import com.tang.entity.Menu;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author tang
 * @date 2022/5/24 10:02
 * @desc
 * 接收前端登录请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Integer id;
    private String username;
    private String password;
    private String nickname;
    private String avatarUrl;
    private String token; // token
    private String role;
    private List<Menu> menus;
}
