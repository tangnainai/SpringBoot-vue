package com.tang.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author tang
 * @since 2022-06-05
 */
@Getter
@Setter
  @TableName("sys_role")
@ApiModel(value = "Role对象", description = "")
public class Role implements Serializable {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty("ID")
    @TableId(value = "id", type = IdType.AUTO)
  private Integer id;

  @ApiModelProperty("名称")
  private String name;

  @ApiModelProperty("描述")
  private String description;

  @ApiModelProperty("修改时间")
  private String time;

  private String flag;

  @TableField(exist = false)
  private String flagRole;
}
