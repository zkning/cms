package com.fast.admin.orm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页数据
 * @author zkning
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pager<T> implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer pageSize;
	private Integer pageNo;
	private Long totalElements;
	private List<T> content;
}
