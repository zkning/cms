package com.fast.admin.mybatis;

import com.sophia.cms.orm.idworker.SnowflakeId;
import tk.mybatis.mapper.genid.GenId;

/**
 * 自定义workId
 */
public class GenerateWorkId implements GenId<Long> {
    @Override
    public Long genId(String s, String s1) {
        return SnowflakeId.getId();
    }
}
