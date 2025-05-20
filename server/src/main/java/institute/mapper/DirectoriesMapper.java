package institute.mapper;

import java.util.List;

import institute.pojo.Directory;

public interface DirectoriesMapper {
    // 定义查询的抽象方法
    // List<Directory> selectDirectories();

    //依据主键查询一条记录
    Directory selectOneById(Integer id);

    // 增删改操作返回的都是int类型
    int save(Directory directory);
    int update(Directory directory);
    int delete(Directory directory);

    //分页功能演示
    List<Directory> selectAllDirectories();

}
