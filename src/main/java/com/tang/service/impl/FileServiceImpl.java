package com.tang.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tang.common.Constants;
import com.tang.entity.FileUrl;
import com.tang.exception.ServiceException;
import com.tang.mapper.FileMapper;
import com.tang.service.IFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tang
 * @since 2022-05-30
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, FileUrl> implements IFileService {
    @Resource
    private FileMapper fileMapper;
    @Value("${files.upload.path}")
    private String fileUploadPath;

    @Override
    public List<FileUrl> selectFile(String fileName) {
        return fileMapper.selectFile(fileName);
    }

    @Override
    public List<FileUrl> selectMd5(String md5) {
        return fileMapper.selectMd5(md5);
    }

    @Override
    public FileUrl selectFileById(Integer id) {
        return fileMapper.selectFileById(id);
    }

    @Override
    public IPage<FileUrl> queryBatch(IPage<FileUrl> page, String name) {
        return fileMapper.queryBatch(page,name);
    }

    @Override
    public Boolean updateIsDelete(Integer id) {
        return fileMapper.updateIsDelete(id);
    }

    @Override
    public Boolean thoroughlyDelete(Integer id) {
        deleteFile(id);
        return fileMapper.thoroughlyDelete(id);
    }

    public void deleteFile(Integer id){
        // 根据id获取url
        String fileName = selectFileById(id).getFileName();
        // 根据数据库url 查询数据库中文件是否还存在 若不存在则删除磁盘中存储的文件
        List<FileUrl> list = selectFile(fileName);
        String url = ""; // 获取文件路径
        if(list.size()==1){
            url = fileUploadPath + list.get(0).getFileName();
            File file = new File(url);
            if(file.exists()){
                file.delete();
            }else {
                throw new ServiceException(Constants.CODE_600,"文件不存在");
            }
        }
    }


    @Override
    public Boolean empty() {
        List<Integer> ids = fileMapper.emptyCount();
        if(ids.size()>0){
            for (Integer id : ids) {
                deleteFile(id);
                fileMapper.thoroughlyDelete(id);
            }
        }else{
            throw new ServiceException(Constants.CODE_600,"回收站没有文件，不需要清空");
        }
        return true;
    }
}
