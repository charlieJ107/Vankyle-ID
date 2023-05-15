package com.vankyle.id.config.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vankyle.id.models.login.AuthenticationResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

public class JsonResponseAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonResponseAuthenticationFailureHandler() {
        super();
        setRedirectStrategy(new JsonResponseRedirectStrategy());
    }
    public JsonResponseAuthenticationFailureHandler(String defaultFailureUrl) {
        super(defaultFailureUrl);
        setRedirectStrategy(new JsonResponseRedirectStrategy());
    }

    /**
     * Handle authentication failed
     *
     * @param request   the request during which the authentication attempt occurred.
     * @param response  the response.
     * @param exception the exception which was thrown to reject the authentication
     *                  request.
     * @throws IOException      throws when read/write request/response failed
     * @throws ServletException throws when servlet failed
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        logger.debug(exception.getMessage());
        if (request.getHeader("Accept").contains(MediaType.APPLICATION_JSON_VALUE)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setCharacterEncoding("UTF-8");
            AuthenticationResponse res = new AuthenticationResponse();
            res.setStatus(401);
            response.getWriter().write(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(res));
            response.flushBuffer();
        } else {
            super.onAuthenticationFailure(request, response, exception);
        }
    }
}
