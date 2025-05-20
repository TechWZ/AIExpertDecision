package institute.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Directory {
    private int id;
    private String name;
    private Integer parentId;
    private int order;
}
