package com.vankyle.id.config.handlers;

import com.vankyle.id.models.login.AuthenticationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class SecurityHandler extends AbstractAuthenticationTargetUrlRequestHandler implements
        AuthenticationSuccessHandler,
        AccessDeniedHandler {
    public SecurityHandler() {
        super();
        this.setRedirectStrategy(new RestfulRedirectStrategy());
    }

    protected final Log logger = LogFactory.getLog(this.getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Setter
    private RequestCache requestCache = new HttpSessionRequestCache();

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
                request.getHeader("Accept").equals(MediaType.APPLICATION_JSON_VALUE)) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
        response.setCharacterEncoding("UTF-8");
        AuthenticationResponse res = new AuthenticationResponse();
        res.setStatus(403);
        response.getWriter().write(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(res));
        response.flushBuffer();
    }


    /**
     * Handle authentication success
     * basically rewrite from {@link SavedRequestAwareAuthenticationSuccessHandler}
     * some code rewrite from {@link SimpleUrlAuthenticationSuccessHandler}
     *
     * @param request        the request which caused the successful authentication
     * @param response       the response
     * @param authentication the <tt>Authentication</tt> object which was created during
     *                       the authentication process.
     * @throws IOException      throws when read/write request/response failed
     * @throws ServletException throws when servlet failed
     */
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        SavedRequest savedRequest = this.requestCache.getRequest(request, response);
        if (savedRequest == null) {
            // Rewrite onAuthenticationSuccess from {@link SimpleUrlAuthenticationSuccessHandler}
            handle(request, response, authentication);
            clearAuthenticationAttributes(request);
            // Rewrite end
            return;
        }
        String targetUrlParameter = getTargetUrlParameter();
        if (isAlwaysUseDefaultTargetUrl()
                || (targetUrlParameter != null && StringUtils.hasText(request.getParameter(targetUrlParameter)))) {
            this.requestCache.removeRequest(request, response);
            // Rewrite onAuthenticationSuccess from {@link SimpleUrlAuthenticationSuccessHandler}
            // Use the method rewrite from SimpleUrlAuthenticationSuccessHandler
            handle(request, response, authentication);
            clearAuthenticationAttributes(request);
            // Rewrite end
            return;
        }
        clearAuthenticationAttributes(request);
        // Use the DefaultSavedRequest URL
        String targetUrl = savedRequest.getRedirectUrl();
        this.getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }


    /**
     * Invokes the configured {@code RedirectStrategy} with the URL returned by the
     * {@code determineTargetUrl} method.
     * <p>
     * The redirect will not be performed if the response has already been committed.
     */
    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);
        if (response.isCommitted()) {
            logger.debug(LogMessage.format("Did not redirect to %s since response already committed.", targetUrl));
            return;
        }
        this.getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /**
     * Removes temporary authentication-related data which may have been stored in the
     * session during the authentication process.
     */
    protected final void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}
