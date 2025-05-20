package institute.service;

import java.util.List;

import com.github.pagehelper.PageInfo;

import institute.pojo.Directory;

public interface DirectoriesService {
    // List<Directory> getDirectories();

    PageInfo<Directory> getAllDirectories(int pageNum, int pageSize);
}
