package institute.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import institute.service.NoteService;

@Service
public class NoteServiceImpl implements NoteService{

    @Override
    public Map<Object, Object> listFilesRecursively(String path, Map<Object, Object> directory) {

        
        // 获取目录下所有文件和子目录
        File[] files = new File(path).listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 如果是目录，则递归进入该目录
                    Map<Object, Object> subDirectory = new HashMap<>();
                    directory.put(file.getAbsolutePath(), subDirectory);

                    listFilesRecursively(file.getAbsolutePath(), subDirectory); // 递归调用
                } else {
                    // 如果是文件，则输出文件名
                    directory.put(file.getAbsolutePath(), file);
                }
            }
        }
            return directory;
    }
    
}
