package com.fast.admin.mybatis.codeGenerator;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MyCodeGenerator {
    private static final String setSuperEntityClass = "com.baomidou.ant.common.BaseEntity";
    private static final String setSuperControllerClass = "com.baomidou.ant.common.BaseController";
    private static final String scanner_tablename_tips = "表名";
    private static final String scanner_modulename_tips = "模块名";
    private static final String setSuperEntityColumns = "id";
    private static final String setTablePrefix = "_";
    private static final String setAuthor = "administrator";
    // 输出路径
    private static final String setOutputDir = "/src/main/java";
    // 包路径
    private static final String setParent = "com.fast.admin";

    // DB
    private static final String setUrl = "jdbc:mysql://localhost:3306/ant?useUnicode=true&useSSL=false&characterEncoding=utf8";
    private static final String setDriverName = "org.gjt.mm.mysql.Driver";
    private static final String setUsername = "user";
    private static final String setPassword = "user";


    // 如果模板引擎是 freemarker
    private static final String templatePath = "/templates/mapper.xml.ftl";
    // 如果模板引擎是 velocity
//    private static final String templatePath = "/templates/mapper.xml.vm";

    // *map.xml 路径
    private static final String xml_mapper_path = "/src/main/resources/mapper/";
    private static final String xml_mapper_suffix = "Mapper";
    private static final String separator = "/";

    /**
     * <p>
     * 读取控制台内容
     * </p>
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        help.append("请输入" + tip + "：");
        System.out.println(help.toString());
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotEmpty(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    public static void main(String[] args) {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + setOutputDir);
        gc.setAuthor(setAuthor);
        gc.setOpen(false);
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(setUrl);
        // dsc.setSchemaName("public");
        dsc.setDriverName(setDriverName);
        dsc.setUsername(setUsername);
        dsc.setPassword(setPassword);
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setModuleName(scanner(scanner_modulename_tips));
        pc.setParent(setParent);
        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };

        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名
                return projectPath +
                        xml_mapper_path +
                        pc.getModuleName() +
                        separator +
                        tableInfo.getEntityName() +
                        xml_mapper_suffix + StringPool.DOT_XML;
            }
        });

        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        // 配置自定义输出模板
        // templateConfig.setEntity();
        // templateConfig.setService();
        // templateConfig.setController();
        templateConfig.setXml(null);
        mpg.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setSuperEntityClass(setSuperEntityClass);
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        strategy.setSuperControllerClass(setSuperControllerClass);
        strategy.setInclude(scanner(scanner_tablename_tips));
        strategy.setSuperEntityColumns(setSuperEntityColumns);
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setTablePrefix(pc.getModuleName() + setTablePrefix);
        mpg.setStrategy(strategy);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }
}
