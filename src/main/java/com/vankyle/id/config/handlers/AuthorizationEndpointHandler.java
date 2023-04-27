package com.vankyle.id.config.handlers;

import com.vankyle.id.models.authorization.AuthorizationErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationException;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.web.OAuth2AuthorizationEndpointFilter;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

public class AuthorizationEndpointHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {
    private final RedirectStrategy redirectStrategy = new RestfulRedirectStrategy();
    private static final Log logger = LogFactory.getLog(AuthorizationEndpointHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Call when authorization or consent success.
     *
     * @param request        the request which caused the successful authentication
     * @param response       the response
     * @param authentication the <tt>Authentication</tt> object which was created during
     *                       the authentication process.
     * @throws IOException Throws when error on read/write request and response stream.
     */
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        logger.debug("Handle authentication Success in oauth2authorizationEndpointHandler");
        OAuth2AuthorizationCodeRequestAuthenticationToken authorizationCodeRequestAuthentication =
                (OAuth2AuthorizationCodeRequestAuthenticationToken) authentication;
        assert authorizationCodeRequestAuthentication.getRedirectUri() != null;
        assert authorizationCodeRequestAuthentication
                .getAuthorizationCode() != null;
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(authorizationCodeRequestAuthentication.getRedirectUri())
                .queryParam(
                        OAuth2ParameterNames.CODE,
                        authorizationCodeRequestAuthentication
                                .getAuthorizationCode()
                                .getTokenValue());
        if (StringUtils.hasText(authorizationCodeRequestAuthentication.getState())) {
            uriBuilder.queryParam(OAuth2ParameterNames.STATE, authorizationCodeRequestAuthentication.getState());
        }
        String redirectUrl = uriBuilder.toUriString();
        response.setCharacterEncoding("UTF-8");
        redirectStrategy.sendRedirect(request, response, redirectUrl);
    }

    /**
     * Handle a restful authorization failed response. Basically rewrite from {@link OAuth2AuthorizationEndpointFilter}
     * @param request the request during which the authentication attempt occurred.
     * @param response the response.
     * @param exception the exception which was thrown to reject the authentication
     * request.
     * @throws IOException Throws when error on read/write request and response stream.
     */
    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {
        logger.debug("Authorized Failure, " + exception.getMessage());

        OAuth2AuthorizationCodeRequestAuthenticationException authorizationCodeRequestAuthenticationException =
                (OAuth2AuthorizationCodeRequestAuthenticationException) exception;
        OAuth2Error error = authorizationCodeRequestAuthenticationException.getError();
        OAuth2AuthorizationCodeRequestAuthenticationToken authorizationCodeRequestAuthentication =
                authorizationCodeRequestAuthenticationException.getAuthorizationCodeRequestAuthentication();

        if (authorizationCodeRequestAuthentication == null ||
                !StringUtils.hasText(authorizationCodeRequestAuthentication.getRedirectUri())) {
            // Error with invalid params so no authentication
            sendError(request, response, error.toString());
            return;
        }
        // Error with valid params, but authentication is failed,
        // redirect to the error handling endpoint with error params
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(authorizationCodeRequestAuthentication.getRedirectUri())
                .queryParam(OAuth2ParameterNames.ERROR, error.getErrorCode());
        if (StringUtils.hasText(error.getDescription())) {
            uriBuilder.queryParam(OAuth2ParameterNames.ERROR_DESCRIPTION, error.getDescription());
        }
        if (StringUtils.hasText(error.getUri())) {
            uriBuilder.queryParam(OAuth2ParameterNames.ERROR_URI, error.getUri());
        }
        if (StringUtils.hasText(authorizationCodeRequestAuthentication.getState())) {
            uriBuilder.queryParam(OAuth2ParameterNames.STATE, authorizationCodeRequestAuthentication.getState());
        }
        String redirectUrl = uriBuilder.toUriString();
        redirectStrategy.sendRedirect(request, response, redirectUrl);
    }

    /**
     * Send a restful error response. When the client declare accepting json, return a json response, otherwise return
     * a default error page.
     * @param request the request during which the authentication attempt occurred.
     * @param response the response.
     * @param errorMessage the error message representing the error details. Mostly formatted from {@link OAuth2Error}.
     * @throws IOException Throws when error on read/write request and response stream.
     */
    private void sendError(
            HttpServletRequest request,
            HttpServletResponse response,
            String errorMessage
    ) throws IOException {
        if (request.getHeader("Accept") != null &&
                request.getHeader("Accept").equals(MediaType.APPLICATION_JSON_VALUE)) {
            response.setStatus(HttpServletResponse.SC_OK);
            AuthorizationErrorResponse res = new AuthorizationErrorResponse();
            res.setStatus(HttpStatus.BAD_REQUEST.value());
            res.setMessage(errorMessage);
            response.getWriter().write(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(res));
            response.flushBuffer();
        } else {
            // spring-authorization-server to do:  Send default html error response
            response.sendError(HttpStatus.BAD_REQUEST.value(), errorMessage);
        }
    }
}
