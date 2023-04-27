package com.vankyle.id.controllers;

import com.vankyle.id.models.consent.ConsentResponse;
import com.vankyle.id.models.consent.Scope;
import com.vankyle.id.service.security.UserManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Controller
public class ConsentController {
    private final RegisteredClientRepository registeredClientRepository;
    private final OAuth2AuthorizationConsentService authorizationConsentService;
    private final UserManager userManager;
    @Value("${vankyle.id.frontend-url}")
    private String frontendUrl;
    @Value("${vankyle.id.restful}")
    private boolean isRestful;
    @Value("${vankyle.id.integrated}")
    private boolean isIntegrated;

    public ConsentController(
            RegisteredClientRepository registeredClientRepository,
            OAuth2AuthorizationConsentService authorizationConsentService,
            UserManager userManager) {
        this.registeredClientRepository = registeredClientRepository;
        this.authorizationConsentService = authorizationConsentService;
        this.userManager = userManager;
    }

    @GetMapping("/consent")
    public String consent(
            Model model,
            Principal principle,
            @RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
            @RequestParam(OAuth2ParameterNames.SCOPE) String scope,
            @RequestParam(OAuth2ParameterNames.STATE) String state) {
        if (isRestful) {
            if (isIntegrated) {
                return "index";
            } else {
                UriBuilder uriBuilder = UriComponentsBuilder.fromUriString(frontendUrl)
                        .path("/consent")
                        .queryParam(OAuth2ParameterNames.CLIENT_ID, clientId)
                        .queryParam(OAuth2ParameterNames.SCOPE, scope)
                        .queryParam(OAuth2ParameterNames.STATE, state);
                return String.format("redirect:%s", uriBuilder.build());
            }

        }

        if (principle == null) {
            return "redirect:/login";
        }

        RegisteredClient registeredClient = this.registeredClientRepository.findByClientId(clientId);
        if (registeredClient == null) {
            return "redirect:/login";
        }
        OAuth2AuthorizationConsent currentAuthorizationConsent =
                this.authorizationConsentService.findById(registeredClient.getClientId(), principle.getName());

        Set<String> authorizedScopes = Collections.emptySet();
        if (currentAuthorizationConsent != null) {
            authorizedScopes = currentAuthorizationConsent.getScopes();
        }
        Set<Scope> scopesToApprove = new HashSet<>();
        Set<Scope> previouslyApprovedScopes = new HashSet<>();
        for (String requestedScope : StringUtils.delimitedListToStringArray(scope, " ")) {
            if (authorizedScopes.contains(requestedScope)) {
                previouslyApprovedScopes.add(new Scope(requestedScope, requestedScope, requestedScope, true));
            } else {
                scopesToApprove.add(new Scope(requestedScope, requestedScope, requestedScope, false));
            }
        }
        var user = userManager.findByUsername(principle.getName());
        if (user.getName() == null) {
            model.addAttribute("principle", principle.getName());
        } else {
            model.addAttribute("principle", user.getName());
        }
        model.addAttribute("clientId", registeredClient.getClientName());
        if (registeredClient.getClientName() == null) {
            model.addAttribute("clientName", registeredClient.getClientId());
        } else {
            model.addAttribute("clientName", registeredClient.getClientName());
        }
        model.addAttribute("userPicture", user.getPicture());
        model.addAttribute("state", state);
        model.addAttribute("scopesToApprove", scopesToApprove);
        model.addAttribute("previouslyApprovedScopes", previouslyApprovedScopes);
        return "consent";
    }


    @GetMapping("/api/consent")
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
