package com.vankyle.id.controllers;

import com.vankyle.id.models.consent.ConsentResponse;
import com.vankyle.id.models.consent.Scope;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Controller
public class ConsentController {
    private final RegisteredClientRepository registeredClientRepository;
    private final OAuth2AuthorizationConsentService authorizationConsentService;


    public ConsentController(
            RegisteredClientRepository registeredClientRepository,
            OAuth2AuthorizationConsentService authorizationConsentService) {
        this.registeredClientRepository = registeredClientRepository;
        this.authorizationConsentService = authorizationConsentService;
    }


    @GetMapping("${vankyle.id.api-path}/consent")
    public @ResponseBody ConsentResponse consent(Principal principal,
                                                 @RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
                                                 @RequestParam(OAuth2ParameterNames.SCOPE) String scope,
                                                 @RequestParam(OAuth2ParameterNames.STATE) String state) {
        ConsentResponse response = new ConsentResponse();
        response.setState(state);
        if (principal == null) {
            response.setStatus(401);
            return response;
        }
        RegisteredClient registeredClient = this.registeredClientRepository.findByClientId(clientId);
        if (registeredClient == null) {
            response.setStatus(500);
            return response;
        }
        OAuth2AuthorizationConsent currentAuthorizationConsent =
                this.authorizationConsentService.findById(registeredClient.getId(), principal.getName());
        Set<String> authorizedScopes;
        if (currentAuthorizationConsent != null) {
            authorizedScopes = currentAuthorizationConsent.getScopes();
        } else {
            authorizedScopes = Collections.emptySet();
        }
        Set<String> scopesToApprove = new HashSet<>();
        Set<String> previouslyApprovedScopes = new HashSet<>();
        for (String requestedScope : StringUtils.delimitedListToStringArray(scope, " ")) {
            if (OidcScopes.OPENID.equals(requestedScope)) {
                continue;
            }
            if (authorizedScopes.contains(requestedScope)) {
                previouslyApprovedScopes.add(requestedScope);
            } else {
                scopesToApprove.add(requestedScope);
            }
        }
        ConsentResponse.Client client = new ConsentResponse.Client();
        client.setClient_id(registeredClient.getClientId());
        client.setClient_name(registeredClient.getClientName());
        ConsentResponse.User user = new ConsentResponse.User();
        user.setPrincipal(principal.getName());
        response.setStatus(200);
        response.setClient(client);
        response.setUser(user);
        response.setScopesToApprove(new HashSet<>());
        response.setPreviouslyApprovedScopes(new HashSet<>());
        scopesToApprove.forEach(scopeId ->
                response.getScopesToApprove().add(new Scope(scopeId, scopeId, scopeId, false)));
        previouslyApprovedScopes.forEach(scopeId ->
                response.getPreviouslyApprovedScopes().add(new Scope(scopeId, scopeId, scopeId, true)));
        return response;
    }
}
