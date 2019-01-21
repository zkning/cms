package com.sophia.cms.rbac.model;

import com.sophia.cms.framework.request.Request;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author liangyonghua
 * @date 2018/7/23 15:00
 */
@Data
public class RbacUserInfoEditModel extends Request {

    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    private String userName;

    @ApiModelProperty(value = "用户id")
    private Long id;

    @ApiModelProperty(value = "用户名字", required = true)
    @NotBlank(message = "真实名称不能为空")
    private String name;

    @ApiModelProperty(value = "手机号码")
    private String mobile;

    @ApiModelProperty(value = "用户邮箱")
    private String email;

    @ApiModelProperty(value = "所属分组", required = true)
    @NotNull(message = "所属分组id不能为空")
    private Long groupId;

    @ApiModelProperty(value = "版本号")
    private Long version;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "角色列表")
    private List<Long> roles;

}
