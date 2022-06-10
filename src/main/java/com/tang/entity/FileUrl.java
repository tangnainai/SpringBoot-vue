package com.tang.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * <p>
 * 
 * </p>
 *
 * @author tang
 * @since 2022-05-30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_file")
@ApiModel(value = "File对象", description = "")
public class FileUrl implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty("id")
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty("文件名称")
      private String name;

      @ApiModelProperty("磁盘文件名称")
      private String fileName;

      @ApiModelProperty("文件类型")
      private String type;

      @ApiModelProperty("文件大小")
      private Long size;
      @ApiModelProperty("文件路径")
      private String url;

      @ApiModelProperty("文件是否存在")
      private String md5;

      @ApiModelProperty("文件删除的时间")
      private String deleteTime;

      @TableLogic
      @ApiModelProperty("逻辑删除")
      private Boolean isDelete;

      @ApiModelProperty("是否禁用链接")
      private Boolean enable;


}
