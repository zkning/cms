package fast.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CodeTemplateModel {

    /**
     * 表名
     */
    private String tableName;

    /**
     * 实体名
     */
    private String entity;
    private String entityVar;

    /**
     * 主键id类型
     */
    private String idType;

    /**
     * 包路径
     */
    private String packageDir;

    /**
     * 模板路径
     */
    private String templateDir;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 输出路径
     */
    private String outputDir;

    /**
     * 输出文件类型JAVA
     */
    private String fileName;

    /**
     * 列信息
     */
    private List<ColModel> colModels;

    @Data
    public static class ColModel {
        private String colName;
        private String colType;
        private String comment;
        private boolean isPri;
    }
}
