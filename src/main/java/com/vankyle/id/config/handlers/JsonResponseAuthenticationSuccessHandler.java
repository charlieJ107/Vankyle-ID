package com.vankyle.id.config.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;

public class JsonResponseAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    public JsonResponseAuthenticationSuccessHandler() {
        super();
        setRedirectStrategy(new JsonResponseRedirectStrategy());
    }
    public JsonResponseAuthenticationSuccessHandler(String defaultTargetUrl) {
        super();
        setDefaultTargetUrl(defaultTargetUrl);
        setRedirectStrategy(new JsonResponseRedirectStrategy());
    }
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
