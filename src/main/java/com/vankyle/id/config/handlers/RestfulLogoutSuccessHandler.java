package com.vankyle.id.config.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import java.io.IOException;

public class RestfulLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
    public RestfulLogoutSuccessHandler() {
        super();
        setRedirectStrategy(new RestfulRedirectStrategy());
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        logger.debug("Handle Logout Success");
        if (request.getHeader("Accept") != null &&
                request.getHeader("Accept").equals("application/json")) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"status\":200,\"message\":\"Logout Success\"}");
        } else {
            super.onLogoutSuccess(request, response, authentication);
        }
    }
}
