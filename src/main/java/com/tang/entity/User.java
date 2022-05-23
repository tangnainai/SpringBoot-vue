package com.tang.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tang
 * @date 2022/5/22 15:30
 * @desc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_user")
public class User {
    @TableId(value = "id")
    private Integer id;
    private String username;
    @JsonIgnore // 忽略这个字段不显示给前端
    private String password;
    private String email;
    private String phone;
    private String nickname;
    private String address;
    @TableLogic
    @TableField(select = false) // 查询不显示改字段
    private Integer deleted;
    private String avatarUrl;
}
