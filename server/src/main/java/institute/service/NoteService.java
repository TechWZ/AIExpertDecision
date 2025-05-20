package institute.service;

import java.util.Map;

public interface NoteService {
    // 递归列出所有文件
    Map<Object, Object> listFilesRecursively(String path, Map<Object, Object> directory);
}
