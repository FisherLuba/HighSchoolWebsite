package com.fisherl.schoolwebsite.listener;

import com.fisherl.schoolwebsite.user.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class LoginListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {

    private UserManager userManager;

    @Autowired
    public LoginListener(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
        if (!(event.getAuthentication().isAuthenticated())) return;
        if (!(event.getAuthentication().getPrincipal() instanceof final OAuth2User principal)) return;
        final String hd = principal.getAttribute("hd");
        if (hd == null || !hd.equalsIgnoreCase("lynnfield.k12.ma.us")) {
            event.getAuthentication().setAuthenticated(false);
            return;
        }
        this.userManager.getOrCreateUser(principal);
    }
}