package com.vankyle.id.config.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import java.io.IOException;

/**
 * Logout Success Handler using Restful redirect strategy
 * @see JsonResponseRedirectStrategy
 * @see SimpleUrlLogoutSuccessHandler
 */
public class JsonResponseLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
    public JsonResponseLogoutSuccessHandler() {
        super();
        setRedirectStrategy(new JsonResponseRedirectStrategy());
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        logger.debug("Handle Logout Success");
        if (request.getHeader("Accept") != null &&
                request.getHeader("Accept").contains(MediaType.APPLICATION_JSON_VALUE)) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"status\":200,\"message\":\"Logout Success\"}");
        } else {
            super.onLogoutSuccess(request, response, authentication);
        }
    }
}
