package com.sophia.cms.rbac.model;

import com.sophia.cms.rbac.domain.Res;
import lombok.Data;
import org.assertj.core.util.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * 授权结果
 * Created by ningzuokun on 2017/12/21.
 */
@Data
public class GetMenuModel implements Serializable {

    private List<Res> resources = Lists.newArrayList();
}
