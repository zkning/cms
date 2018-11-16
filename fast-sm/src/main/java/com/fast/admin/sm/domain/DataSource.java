package com.fast.admin.sm.domain;
import com.fast.admin.orm.domain.Auditable;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "t_sm_datasource")
public class DataSource extends Auditable {

	/**
	 * 驱动名
         */
    	private String driverClassName;

    		
	/**
	 * 
         */
    	private String url;

    		
	/**
	 * 用户名
         */
    	private String dbUsername;

    		
	/**
	 * 密码
         */
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
