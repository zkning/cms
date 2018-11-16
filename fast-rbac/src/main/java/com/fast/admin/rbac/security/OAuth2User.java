package com.fast.admin.rbac.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
public class OAuth2User implements UserDetails {
    private static final long serialVersionUID = 5846117211846153374L;

    @JsonIgnore
    private String password;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @JsonIgnore
    @ApiModelProperty(value = "姓名")
    private String name;

    @JsonIgnore
    @ApiModelProperty(value = "邮箱")
    private String email;

    @JsonIgnore
    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "所属组别id")
    private Long groupId;

    @JsonIgnore
    @ApiModelProperty(value = "是否可用")
    private Boolean enables;

    @JsonIgnore
    @ApiModelProperty(value = "账户过期否")
    private boolean accountNonExpired;

    @JsonIgnore
    @ApiModelProperty(value = "证书过期否")
    private boolean credentialsNonExpired;

    @JsonIgnore
    @ApiModelProperty(value = "账户锁定否")
    private boolean accountNonLocked;

    /**
     * 授权项
     */
    private Collection<? extends GrantedAuthority> authorities;

    public void setAuthorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enables;
    }


}
