package fast.admin;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Goal which touches a timestamp file.
 *
 * @goal touch
 * @phase process-sources
 */
@Mojo(name = "create")
public class CodeGeneratorMojo
        extends AbstractMojo {

    private static final String in_url = "url";
    private static final String in_username = "username";
    private static final String in_password = "password";
    private static final String in_tableName = "table_name";
    private static final String in_entity = "entity";
    private static final String in_packageDir = "package_dir";
    private static final String in_outputDir = "output_dir";

    // target路径(模板暂存路径)

    @Parameter(required = true)
    private String name;

    public void execute() throws MojoExecutionException {
        CodeTemplateModel codeTemplateModel = CodeTemplateModel.builder()
                .templateDir(name)
                .outputDir(getenv(in_outputDir))
                .packageDir(getenv(in_packageDir))
                .tableName(getenv(in_tableName))
                .entity(getenv(in_entity))
                .build();
        CodeGeneratorService codeGeneratorService = new CodeGeneratorService();
        try {
            codeGeneratorService.setUrl(getenv(in_url));
            codeGeneratorService.setUsername(getenv(in_username));
            codeGeneratorService.setPassword(getenv(in_password));
            codeGeneratorService.generator(codeTemplateModel);
        } catch (Exception e) {
            getLog().error(e);
        }
    }

    private String getenv(String name) {
        return System.getenv(name);
    }
}
