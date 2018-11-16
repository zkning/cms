package com.fast.admin.rbac.security;

import com.fast.admin.rbac.domain.RbacUserInfo;
import com.fast.admin.rbac.domain.Resources;
import com.fast.admin.rbac.domain.Role;
import com.fast.admin.rbac.service.RbacUserService;
import com.fast.admin.rbac.service.ResourcesService;
import com.fast.admin.rbac.service.RoleService;
import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.util.Lists;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public class SecurityUserDetailService implements UserDetailsService {

    @Autowired
    RbacUserService rbacUserService;

    @Autowired
    ResourcesService resourcesService;

    @Autowired
    RoleService roleService;


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        RbacUserInfo userInfo = rbacUserService.findByUserName(s);
        if (null == userInfo) {
            throw new UsernameNotFoundException("帐户名或密码不正确，请重新输入");
        }

        // 获取用户权限
        List<GrantedAuthority> auths = Lists.newArrayList();
        List<Resources> resources = resourcesService.findAllResourcesByUserId(userInfo.getId());
        if (CollectionUtils.isNotEmpty(resources)) {
            for (Resources res : resources) {
                SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(res.getCode());
                auths.add(simpleGrantedAuthority);
            }
        }

        // 角色
        List<Role> roles = roleService.findAllRoleByUserId(userInfo.getId());
        if (CollectionUtils.isNotEmpty(roles)) {
            for (Role role : roles) {
                SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(role.getRoleCode());
                auths.add(simpleGrantedAuthority);
            }
        }
        OAuth2User oAuth2User = new OAuth2User();
        oAuth2User.setAuthorities(auths);
        oAuth2User.setEnables(true);
        oAuth2User.setAccountNonLocked(true);
        oAuth2User.setAccountNonExpired(true);
        oAuth2User.setCredentialsNonExpired(true);
        new ModelMapper().map(userInfo, oAuth2User);
        return oAuth2User;
    }
}
