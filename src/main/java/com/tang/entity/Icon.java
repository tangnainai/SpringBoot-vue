package com.tang.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2022-06-06
 */
@Getter
@Setter
  @ApiModel(value = "Icon对象", description = "")
@TableName("sys_icon")
public class Icon implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId
      private Integer id;
      @ApiModelProperty("图标名字")
      private String name;

      @ApiModelProperty("内容")
      private String value;

      @ApiModelProperty("类型")
      private String type;


}
