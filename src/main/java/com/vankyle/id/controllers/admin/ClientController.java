package com.vankyle.id.controllers.admin;

import com.vankyle.id.models.admin.client.ClientItem;
import com.vankyle.id.models.admin.client.ClientItemResponse;
import com.vankyle.id.models.admin.client.ClientListResponse;
import com.vankyle.id.service.oauth.RegisteredClientService;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("${vankyle.id.api-path}/admin/client")
public class ClientController {
    private final RegisteredClientService registeredClientService;

    public ClientController(RegisteredClientService registeredClientService) {
        this.registeredClientService = registeredClientService;
    }

    @GetMapping("/")
    public ClientListResponse getClients() {
        var response = new ClientListResponse();
        response.setStatus(2000);
        response.setClients(registeredClientService.findAll());
        return response;
    }

    @GetMapping("/{id}")
    public ClientItemResponse getClient(@PathVariable String id) {
        var client = registeredClientService.findById(id);
        if (client == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
        }
        var response = new ClientItemResponse();
        response.setStatus(2000);
        var clientItem = new ClientItem(client);
        clientItem.setClientSecret(null);
        response.setClient(clientItem);
        return response;
    }

    @PostMapping("/")
    public ClientItemResponse createClient(@RequestBody ClientItem client) {
        return saveClientAndResponse(client);
    }

    private ClientItemResponse saveClientAndResponse(ClientItem client) {
        if (!(
                client.getTokenSettings().getAccessTokenTimeToLive() > 0 &&
                client.getTokenSettings().getRefreshTokenTimeToLive() > 0 &&
                client.getTokenSettings().getAuthorizationCodeTimeToLive() > 0
        )) {
            var response = new ClientItemResponse();
            response.setStatus(4000);
            response.setMessage("Token time to live must be greater than 0");
            return response;
        }
        if (client.getAuthorizationGrantTypes().isEmpty()){
            var response = new ClientItemResponse();
            response.setStatus(4000);
            response.setMessage("Authorization grant types must not be empty");
            return response;
        }
        if (client.getRedirectUris().isEmpty()){
            var response = new ClientItemResponse();
            response.setStatus(4000);
            response.setMessage("Redirect uris must not be empty");
            return response;
        }
        client.getScopes().add(OidcScopes.OPENID);
        var registeredClient = client.toRegisteredClient();
        registeredClientService.save(registeredClient);
        var response = new ClientItemResponse();
        response.setStatus(2000);
        var clientItem = new ClientItem(registeredClient);
        clientItem.setClientSecret(null);
        response.setClient(clientItem);
        return response;
    }

    @PutMapping("/{id}")
    public ClientItemResponse updateClient(@PathVariable String id, @RequestBody ClientItem client) {
        if (!id.equals(client.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client id not match");
        }
        if (registeredClientService.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
        }
        return saveClientAndResponse(client);
    }
}
