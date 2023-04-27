package com.vankyle.id.models.login;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ResetPasswordResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 9068880839660790612L;
    /**
     * 200: success
     * 400: bad request: code is null
     * 401: unauthorized: code is invalid
     * 500: internal server error, happens when the error occurs when validating the totp code
     */
    private int status;
}
