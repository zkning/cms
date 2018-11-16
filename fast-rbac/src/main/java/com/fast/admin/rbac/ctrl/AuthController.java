package com.fast.admin.rbac.ctrl;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
public class AuthController {

    @RequestMapping("/login")
    public String login() {
        return "/index";
    }

    @RequestMapping("/signUp")
    public String signUp() {
        return "/sign_up";
    }

    @RequestMapping("/forgot")
    public String forgot() {
        return "/forgot";
    }

    @RequestMapping("/home")
    public String home() {
        return "/home";
    }

    @ResponseBody
    @RequestMapping("/auth/userInfo")
    public Authentication userInfo(Authentication user) {
        return user;
    }

    @GetMapping("/oauth/user/me")
    @ResponseBody
    public Principal user(Principal user) {
        return user;
    }
}
