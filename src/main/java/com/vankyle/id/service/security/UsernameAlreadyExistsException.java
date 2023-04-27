package com.vankyle.id.service.security;

import java.io.Serial;

public class UsernameAlreadyExistsException extends Exception {
    @Serial
    private static final long serialVersionUID = -4868413367955723655L;

    public UsernameAlreadyExistsException(String userAlreadyExists) {
    }
}
