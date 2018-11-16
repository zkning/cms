package com.fast.admin.sm.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.sql.Timestamp;

@Data
public class DataSourceEditModel{
	
    		
     @ApiModelProperty(value = "id")
    	private Long id;

    		
	 @ApiModelProperty(value = "驱动名")
    	private String driverClassName;

    		
	 @ApiModelProperty(value = "")
    	private String url;

    		
	 @ApiModelProperty(value = "用户名")
    	private String dbUsername;

    		
	 @ApiModelProperty(value = "密码")
    	private String dbPassword;

    		
	 @ApiModelProperty(value = "描述")
    	private String remark;

    		
	 @ApiModelProperty(value = "名称")
    	private String name;

    		
	 @ApiModelProperty(value = "版本号")
    	private Long version;
}
