package com.fast.admin.sm.model;

import com.fast.admin.framework.request.Request;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * Created by lenovo on 2017/9/8.
 */
@Data
public class ZtreeModel extends Request {

    @NotBlank
    private Long sqlId;
    @NotBlank
    private String idKey;
    @NotBlank
    private String name;
    @NotBlank
    private String pidKey;
    @NotBlank
    private String scope;
    @NotNull
    private boolean enable;

    //点击id
    private Long id;
}
