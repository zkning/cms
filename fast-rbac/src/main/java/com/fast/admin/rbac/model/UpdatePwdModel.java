package com.fast.admin.rbac.model;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class UpdatePwdModel {

    @NotBlank(message = "旧密码不能为空")
    private String password;

    @NotBlank(message = "新密码不能为空")
    private String newPassword;

    @NotBlank(message = "新密码不能为空")
    private String newPassword2;
}
