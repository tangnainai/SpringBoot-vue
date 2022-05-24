package com.tang.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String username;
    private String password;
    private String nickname;
    private String avatarUrl;
}
