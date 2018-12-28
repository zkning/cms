package com.fast.admin.mybatis;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * http://mybatis.tk/
 */
public interface BasicMapper<T> extends Mapper<T>, MySqlMapper<T> {
}
