package com.tang.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * <p>
 * 
 * </p>
 *
 * @author tang
 * @since 2022-06-06
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
  @TableName("role_menu")
@ApiModel(value = "RoleMenu对象", description = "")
public class RoleMenu implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty("role的id")
      private Integer roleId;
      @ApiModelProperty("menu的id")
      private Integer menuId;

}
