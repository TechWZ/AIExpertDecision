package institute.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import institute.mapper.DirectoriesMapper;
import institute.pojo.Directory;
import institute.service.DirectoriesService;

@Service
public class DirectoriesServiceImpl implements DirectoriesService {
    // 注入mapper对象
    @Autowired
    private DirectoriesMapper directoriesMapper;

    // @Override
    // public List<Directory> getDirectories() {
    //     return directoriesMapper.selectDirectories();
    // }

    @Override
    public PageInfo<Directory> getAllDirectories(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);//pageNum查第几页，pageSize每页显示几条
        List<Directory> directories = directoriesMapper.selectAllDirectories();
        // PageInfo是分页查询所有查询结果封装的类，所有的结果都从这个类取
        // 使用PageInfo
        PageInfo<Directory> info = new PageInfo<>(directories);
        return info;
    }
}
