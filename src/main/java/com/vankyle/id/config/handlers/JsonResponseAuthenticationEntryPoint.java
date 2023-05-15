package com.vankyle.id.config.handlers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.log.LogMessage;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.io.IOException;

public class JsonResponseAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint
        implements AuthenticationEntryPoint {

    private static final Log logger = LogFactory.getLog(JsonResponseAuthenticationEntryPoint.class);
    @Getter
    @Setter
    private RedirectStrategy redirectStrategy = new JsonResponseRedirectStrategy();

    @Value("${vankyle.id.api-path}")
    private String apiPath = "/api";

    /**
     * @param loginFormUrl URL where the login page can be found. Should either be
     *                     relative to the web-app context path (include a leading {@code /}) or an absolute
     *                     URL.
     */
    public JsonResponseAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    /**
     * Performs the redirect (or forward) to the login form URL.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        if (request.getRequestURI().startsWith(apiPath)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        if (!this.isUseForward()) {
            // redirect to login page. Use https if forceHttps true
            String redirectUrl = buildRedirectUrlToLoginPage(request, response, authException);
            this.redirectStrategy.sendRedirect(request, response, redirectUrl);
            return;
        }
        String redirectUrl = null;
        if (this.isForceHttps() && "http".equals(request.getScheme())) {
            // First redirect the current request to HTTPS. When that request is received,
            // the forward to the login page will be used.
            redirectUrl = buildHttpsRedirectUrlForRequest(request);
        }
        if (redirectUrl != null) {
            this.redirectStrategy.sendRedirect(request, response, redirectUrl);
            return;
        }
        String loginForm = determineUrlToUseForThisRequest(request, response, authException);
        logger.debug(LogMessage.format("Server side forward to: %s", loginForm));
        RequestDispatcher dispatcher = request.getRequestDispatcher(loginForm);
        dispatcher.forward(request, response);
        return;
    }

}
