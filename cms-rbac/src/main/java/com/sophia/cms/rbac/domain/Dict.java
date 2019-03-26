package com.sophia.cms.rbac.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sophia.cms.orm.domain.Auditable;
import lombok.Data;

@Data
@TableName(value = "t_rbac_dict")
public class Dict extends Auditable {
    private String text;
    private String value;
    private Long pid;
    private Integer sort;
    private String remark;
}
