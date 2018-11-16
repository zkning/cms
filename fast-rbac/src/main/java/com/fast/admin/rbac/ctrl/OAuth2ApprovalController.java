package com.fast.admin.rbac.ctrl;

import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 自定义确认页
 */
@Controller
@SessionAttributes("authorizationRequest")
public class OAuth2ApprovalController {

    @RequestMapping("/oauth/confirm_access")
    public String getAccessConfirmation(Map<String, Object> model, HttpServletRequest request) throws Exception {
        AuthorizationRequest authorizationRequest = (AuthorizationRequest) model.get("authorizationRequest");
        request.getSession().setAttribute("clientId", authorizationRequest.getClientId());
        return "oauth_approval";
    }
}