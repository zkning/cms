package com.sophia.cms.sm.domain;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sophia.cms.orm.domain.Auditable;
import lombok.Data;


@Data
@TableName(value = "t_sm_datasource")
public class DataSource extends Auditable {

	/**
	 * 驱动名
         */
	@TableField(value = "driver_class_name")
    	private String driverClassName;

    		
	/**
	 * 
         */
    	private String url;

    		
	/**
	 * 用户名
         */
	@TableField(value = "db_username")
    	private String dbUsername;

    		
	/**
	 * 密码
         */
	@TableField(value = "db_password")
    	private String dbPassword;

    		
	/**
	 * 描述
         */
    	private String remark;

    		
	/**
	 * 名称
         */
    	private String name;
}
