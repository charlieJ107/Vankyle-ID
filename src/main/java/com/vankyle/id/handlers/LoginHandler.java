package com.vankyle.id.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class LoginHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Setter
    private String targetUrlParameter = null;

    private boolean useReferer = false;
    @Setter
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Setter
    private RequestCache requestCache = new HttpSessionRequestCache();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        SavedRequest savedRequest = this.requestCache.getRequest(request, response);
        String targetUrl = "/";
        if (savedRequest == null) {
            boolean alwaysUseDefaultTargetUrl = false;
            if (!alwaysUseDefaultTargetUrl) {
                String value = request.getParameter(this.targetUrlParameter);
                if (StringUtils.hasText(value)) {
                    targetUrl = value;
                }
                if (response.isCommitted()) {
                    this.logger.debug(LogMessage.format("Did not redirect to %s since response already committed.", targetUrl));
                }
            }
        } else {
            targetUrl = savedRequest.getRedirectUrl();
        }
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
        // If "Accept" header is application/json, return JSON response
        if (request.getHeader("Accept").contains("application/json")) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(this.objectMapper.writeValueAsString(new LoginSuccessResponse(targetUrl)));
        } else {
            this.redirectStrategy.sendRedirect(request, response, targetUrl);
        }
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (response.isCommitted()) {
            this.logger.debug("Response has already been committed. Unable to send error response.");
            return;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(this.objectMapper.writeValueAsString(new LoginFailureResponse(exception.getMessage())));

    }

    @Data
    public static class LoginSuccessResponse {
        private String redirectUrl;

        public LoginSuccessResponse(String redirectUrl) {
            this.redirectUrl = redirectUrl;
        }
    }

    @Data
    public static class LoginFailureResponse {
        private String message;

        public LoginFailureResponse(String message) {
            this.message = message;
        }
    }

}
