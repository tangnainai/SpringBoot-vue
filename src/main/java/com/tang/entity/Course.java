package com.tang.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2022-06-10
 */
@Getter
@Setter
  @TableName("sys_course")
@ApiModel(value = "Course对象", description = "")
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty("id")
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty("课程名")
      private String name;

      @ApiModelProperty("学分")
      private Integer score;

      @ApiModelProperty("上课时间")
      private String times;

      @ApiModelProperty("是否开课")
      private Boolean state;

      @ApiModelProperty("授课老师id")
      private Integer teacherId;

      @TableField(exist = false)
      private String teacher;
}
