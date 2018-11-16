package ${packageDir}.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.sql.Timestamp;

@Data
public class ${entity}FetchModel {
	
	<#list colModels as col> 		
    		
	@ApiModelProperty(value = "${col.comment}")
    	private ${col.colType} ${col.colName};

    </#list>
}
