package com.vankyle.id.models.login;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AuthenticationResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 2999527491888333475L;
    private int status;
}
