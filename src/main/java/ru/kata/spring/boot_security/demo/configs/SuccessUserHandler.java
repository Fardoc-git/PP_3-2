package ru.kata.spring.boot_security.demo.configs;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;

@Component
public class SuccessUserHandler implements AuthenticationSuccessHandler {
    // Spring Security использует объект Authentication, пользователя авторизованной сессии.

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    protected void successHandler(HttpServletRequest httpServletRequest,
                                  HttpServletResponse httpServletResponse,
                                  Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(authentication);

        redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, targetUrl);
    }

    protected String determineTargetUrl(Authentication authentication) {
        boolean isUser = false;
        boolean isAdmin = false;
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for(GrantedAuthority grantedAuthority : authorities) {
            if (grantedAuthority.getAuthority().equals(("ROLE_USER"))) {
                isUser = true;
                break;
            } else if (grantedAuthority.getAuthority().equals(("ROLE_ADMIN"))) {
                isAdmin = true;
                break;
            }
        }

        if(isUser) {
            return "user/user-list_";
        } else if (isAdmin) {
            return "admin/user-list";
        } else {
            return "login";
        }
    }

    protected void clearAuthenticationAttributes(HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession(false);
        if (httpSession == null) {
            return;
        }
        httpSession.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse,
                                        Authentication authentication) throws IOException {
        successHandler(httpServletRequest, httpServletResponse, authentication);
        clearAuthenticationAttributes(httpServletRequest);
    }
}