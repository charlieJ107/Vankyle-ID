package com.vankyle.id.config.handlers;

import com.vankyle.id.models.login.RedirectResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.log.LogMessage;
import org.springframework.http.MediaType;
import org.springframework.security.web.DefaultRedirectStrategy;

import java.io.IOException;

public class RestfulRedirectStrategy extends DefaultRedirectStrategy {
    @Override
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        String redirectUrl = calculateRedirectUrl(request.getContextPath(), url);
        if (request.getHeader("Accept") != null &&
                request.getHeader("Accept").equals(MediaType.APPLICATION_JSON_VALUE)) {
            this.logger.debug(LogMessage.format("Send RedirectResponse with url %s", redirectUrl));
            RedirectResponse res = new RedirectResponse();
            res.setStatus(302);
            res.setRedirectUrl(redirectUrl);
            ObjectMapper objectMapper = new ObjectMapper();
            response.getWriter().write(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(res));
            response.flushBuffer();
        } else {
            redirectUrl = response.encodeRedirectURL(redirectUrl);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug(LogMessage.format("Redirecting to %s", redirectUrl));
            }
            response.sendRedirect(redirectUrl);
        }
    }
}