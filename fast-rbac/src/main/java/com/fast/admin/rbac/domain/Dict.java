package com.fast.admin.rbac.domain;

import com.fast.admin.orm.domain.Auditable;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "t_rbac_dict")
public class Dict extends Auditable {
    private String text;
    private String value;
    private Long pid;
    private Integer sort;
    private String remark;
}
