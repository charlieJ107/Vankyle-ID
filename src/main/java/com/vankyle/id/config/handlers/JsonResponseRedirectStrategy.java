package com.vankyle.id.config.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vankyle.id.models.login.RedirectResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.log.LogMessage;
import org.springframework.http.MediaType;
import org.springframework.security.web.DefaultRedirectStrategy;

import java.io.IOException;

public class JsonResponseRedirectStrategy extends DefaultRedirectStrategy {
    @Override
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        if (request.getHeader("Accept") != null &&
                request.getHeader("Accept").contains(MediaType.APPLICATION_JSON_VALUE)) {
            String redirectUrl = calculateRedirectUrl(request.getContextPath(), url);
            this.logger.debug(LogMessage.format("Send RedirectResponse with url %s", redirectUrl));
            RedirectResponse res = new RedirectResponse();
            res.setStatus(302);
            res.setRedirectUrl(redirectUrl);
            ObjectMapper objectMapper = new ObjectMapper();
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(res));
            response.flushBuffer();
        } else {
            super.sendRedirect(request, response, url);
        }
    }
}