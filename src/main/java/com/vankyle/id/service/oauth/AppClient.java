package com.vankyle.id.service.oauth;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.io.Serial;
import java.io.Serializable;

public class AppClient extends RegisteredClient implements Serializable, CredentialsContainer {
    @Serial
    private static final long serialVersionUID = 5037690849056103277L;

    @Override
    public void eraseCredentials() {
    }
}
