package com.pkce.poc.pkcepocfrontend.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class CustomLogoutHandler implements LogoutHandler {

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
                       Authentication authentication) {
        System.out.println("logout handler");
        try {
            response.sendRedirect("http://192.168.4.86:8080/auth/realms/TestPKCE/protocol/openid-connect/logout?redirect_uri=http://192.168.4.64:20001/relogin");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
