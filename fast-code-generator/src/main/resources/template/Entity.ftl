package ${packageDir}.domain;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "${tableName}")
public class ${entity} extends Auditable {
    

    <#list colModels as col> 
    		
	/**
	 * ${col.comment}
         */
    	private ${col.colType} ${col.colName};

    </#list>
}
