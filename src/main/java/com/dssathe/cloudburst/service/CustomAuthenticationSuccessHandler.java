package com.dssathe.cloudburst.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.ServletException;
import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements org.springframework.security.web.authentication.AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(javax.servlet.http.HttpServletRequest httpServletRequest, javax.servlet.http.HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().toString();
        System.out.println(auth.getName().toString());
        System.out.println(auth.getPrincipal().toString());
        System.out.println(role);
        String targetUrl = "";
        if(role.contains("ROLE_ADMIN")) {
            targetUrl = "/adminDashboard";
        } else if(role.contains("ROLE_USER")) {
            targetUrl = "/welcome";
        }
        httpServletResponse.sendRedirect(httpServletResponse.encodeURL(targetUrl));
    }
}