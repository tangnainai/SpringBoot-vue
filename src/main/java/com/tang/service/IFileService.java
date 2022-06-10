package com.tang.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tang.entity.FileUrl;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tang
 * @since 2022-05-30
 */
public interface IFileService extends IService<FileUrl> {
    // 查看文件是否存在，包括逻辑删除的
    List<FileUrl> selectFile(String fileName);
    // 查询文件MD5 是否存在
    List<FileUrl> selectMd5(@Param("fileName")String md5);
    // 查询所有文件,包括逻辑删除
    FileUrl selectFileById(Integer id);
    // 查询逻辑删除的文件
    IPage<FileUrl> queryBatch(IPage<FileUrl> page,String name);
    // 恢复回收站的文件
    Boolean updateIsDelete(Integer id);
    // 彻底删除
    Boolean thoroughlyDelete(Integer id);
    public void deleteFile(Integer id);

    Boolean empty();
}
