package com.vankyle.id.config.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vankyle.id.models.login.AuthenticationResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

import java.io.IOException;

public class JsonResponseAccessDeniedHandler extends AccessDeniedHandlerImpl {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Handle 403 Access denied
     *
     * @param request               that resulted in an <code>AccessDeniedException</code>
     * @param response              so that the user agent can be advised of the failure
     * @param accessDeniedException that caused the invocation
     * @throws IOException      throws when read/write request/response failed
     * @throws ServletException throws when servlet failed
     */
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        logger.debug("Handle 403 Exception");
        logger.debug(accessDeniedException.getMessage());
        if (request.getHeader("Accept") != null &&
                request.getHeader("Accept").contains(MediaType.APPLICATION_JSON_VALUE)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setCharacterEncoding("UTF-8");
            AuthenticationResponse res = new AuthenticationResponse();
            res.setStatus(403);
            response.getWriter().write(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(res));
            response.flushBuffer();
        } else {
            super.handle(request, response, accessDeniedException);
        }

    }



}
