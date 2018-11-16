package fast.admin;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author ningzuokun
 */
public class CodeGeneratorService {
    private static final String FILE_TYPE = ".java";
    private static final String ID_TYPE = "Long";

    // 模板文件
    private static final String TEMPLATE_ZIP = "/template.zip";

    // 模板解压文件夹
    private static final String TEMPLATE_FILE = File.separator + "template";

    /**
     * 连接字符串
     */
    String url;

    /**
     * 用户名
     */
    String username;

    /**
     * 密码
     */
    String password;

    public void generator(CodeTemplateModel codeTemplateModel) throws Exception {
        ifFileNoExists(codeTemplateModel.getOutputDir());
        SqlService sqlService = new SqlService(url, username, password);

        // 实体变量
        codeTemplateModel.setEntityVar(toVar(codeTemplateModel.getEntity()));

        // 获取表列信息
        List<CodeTemplateModel.ColModel> cols = sqlService.execQuery(codeTemplateModel.getTableName());
        codeTemplateModel.setColModels(cols);

        //获取主键类型
        codeTemplateModel.setIdType(getPriType(cols));

        // 获取模板文件
        InputStream inputStream = this.getClass().getResourceAsStream(TEMPLATE_ZIP);

        // 解压缩模板
        unzip(inputStream, codeTemplateModel.getTemplateDir() + File.separator);
        try {

            // 生成模板代码
            this.createEntity(codeTemplateModel);
            this.createController(codeTemplateModel);
            this.createEditModel(codeTemplateModel);
            this.createFetchModel(codeTemplateModel);
            this.createRepository(codeTemplateModel);
            this.createSearchModel(codeTemplateModel);
            this.createService(codeTemplateModel);
        } finally {

            //删除解压的所有模板文件
            File templateFile = new File(codeTemplateModel.getTemplateDir() + TEMPLATE_FILE);
            if (null != templateFile && templateFile.exists()) {
                File[] files = templateFile.listFiles();
                for (File file : files) {
                    file.delete();
                }
                System.out.println(templateFile.delete());
            }
        }
    }

    private String getPriType(List<CodeTemplateModel.ColModel> cols) {
        for (CodeTemplateModel.ColModel colModel : cols) {
            if (colModel.isPri()) {
                return colModel.getColType();
            }
        }
        return ID_TYPE;
    }

    private void createEditModel(CodeTemplateModel codeTemplateModel) throws Exception {
        codeTemplateModel.setTemplateName("EditModel.ftl");
        codeTemplateModel.setFileName(codeTemplateModel.getEntity() + "EditModel" + FILE_TYPE);
        create(codeTemplateModel, "/model/");
    }

    private void createFetchModel(CodeTemplateModel codeTemplateModel) throws Exception {
        codeTemplateModel.setTemplateName("FetchModel.ftl");
        codeTemplateModel.setFileName(codeTemplateModel.getEntity() + "FetchModel" + FILE_TYPE);
        create(codeTemplateModel, "/model/");
    }

    private void createSearchModel(CodeTemplateModel codeTemplateModel) throws Exception {
        codeTemplateModel.setTemplateName("SearchModel.ftl");
        codeTemplateModel.setFileName(codeTemplateModel.getEntity() + "SearchModel" + FILE_TYPE);
        create(codeTemplateModel, "/model/");
    }

    private void createEntity(CodeTemplateModel codeTemplateModel) throws Exception {
        codeTemplateModel.setTemplateName("Entity.ftl");
        codeTemplateModel.setFileName(codeTemplateModel.getEntity() + FILE_TYPE);
        create(codeTemplateModel, "/domain/");
    }

    private void createController(CodeTemplateModel codeTemplateModel) throws Exception {
        codeTemplateModel.setTemplateName("Controller.ftl");
        codeTemplateModel.setFileName(codeTemplateModel.getEntity() + "Controller" + FILE_TYPE);
        create(codeTemplateModel, "/ctrl/");
    }

    private void createService(CodeTemplateModel codeTemplateModel) throws Exception {
        codeTemplateModel.setTemplateName("Service.ftl");
        codeTemplateModel.setFileName(codeTemplateModel.getEntity() + "Service" + FILE_TYPE);
        create(codeTemplateModel, "/service/");
    }

    private void createRepository(CodeTemplateModel codeTemplateModel) throws Exception {
        codeTemplateModel.setTemplateName("Repository.ftl");
        codeTemplateModel.setFileName(codeTemplateModel.getEntity() + "Repository" + FILE_TYPE);
        create(codeTemplateModel, "/repository/");
    }


    private void create(CodeTemplateModel codeTemplateModel, String filePath) throws Exception {
        Writer out = null;
        try {

            //检查代码输出路径,不存在即创建文件
            String path = codeTemplateModel.getOutputDir() + filePath;
            ifFileNoExists(path);

            //第一步：实例化Freemarker的配置类
            Configuration conf = new Configuration();

            //第二步：给配置类设置路径
            conf.setDirectoryForTemplateLoading(new File(codeTemplateModel.getTemplateDir() + TEMPLATE_FILE));
            Template template = conf.getTemplate(codeTemplateModel.getTemplateName());

            //第三步：处理模板及数据之间将数据与模板合成
            //第四步：输出
            out = new FileWriter(new File(path + codeTemplateModel.getFileName()));
            template.process(codeTemplateModel, out);
        } finally {
            if (null != out) {
                out.flush();
                out.close();
            }
        }
    }

    private String toVar(String entity) {
        String header = String.valueOf(entity.charAt(0)).toLowerCase();
        return entity.replace(entity.charAt(0), header.charAt(0));
    }

    public static void unzip(InputStream inputStream, String location) throws IOException {
        File f = new File(location);
        if (!f.isDirectory()) {
            f.mkdirs();
        }
        ZipInputStream zin = new ZipInputStream(inputStream);
        try {
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                String path = location + ze.getName();
                if (ze.isDirectory()) {
                    File unzipFile = new File(path);
                    if (!unzipFile.isDirectory()) {
                        unzipFile.mkdirs();
                    }
                } else {
                    FileOutputStream fout = new FileOutputStream(path, false);
                    try {
                        for (int c = zin.read(); c != -1; c = zin.read()) {
                            fout.write(c);
                        }
                        zin.closeEntry();
                    } finally {
                        fout.close();
                    }
                }
            }
        } finally {
            if (null != zin) {
                zin.close();
            }
        }
    }

    /**
     * 如果目录不存在创建
     *
     * @param path
     */
    public void ifFileNoExists(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
