package institute.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;

import institute.pojo.Chat; // 确保 Chat 类已导入
import institute.pojo.Directory;
import institute.service.DirectoriesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@Tag(name = "目录管理模块", description = "目录管理模块接口")
public class BaseController {
    // 注入service层对象
    @Autowired
    private DirectoriesService directoriesService;

    @GetMapping("getChat")
    // api doc
    @Operation(summary = "会话模块-获取会话")
    // @Parameter(name="id", description = "查询参数-目录id", required = true)
    public Chat getChat(){
        // 创建一个 Chat 对象的假数据
        Chat mockChat = new Chat("session_123", "与AI的演示对话", "这是最后一条消息的预览。");
        // 如果 Chat 类有 setId 方法并且你需要设置它：
        // mockChat.setId(1L); 
        // lastActivityTimestamp 会在 Chat 构造函数中自动设置为 LocalDateTime.now()
        return mockChat;
    }

    @GetMapping("getDirectoryById/{id}")
    public Directory select(@PathVariable int id){
        return null;
    }

    @PostMapping("directory")
    public Integer save(@RequestBody Directory entity) {
        
        return null;
    }
    
    @PutMapping("updateDirectory/{id}")
    public String updateDirectory(@RequestBody Directory entity) {
        
        return null;
    }

    @DeleteMapping("delDirectory/{id}")
    public int delete(@PathVariable Integer id){
        return 0;
    }

    @GetMapping("directory/list")
    public PageInfo<Directory> selectAll(int pageNum, int pageSize){
        return directoriesService.getAllDirectories(pageNum, pageSize);
    }
}
