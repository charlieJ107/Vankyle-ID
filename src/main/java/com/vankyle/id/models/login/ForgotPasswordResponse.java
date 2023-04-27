package com.vankyle.id.models.login;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ForgotPasswordResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1050826942521404021L;
    /**
     * Status code
     * 200: Success: The verification code is sent to the email
     * 400: Bad request: Invalid request body
     * 404: Not found: The user does not exist
     * 500: Internal server error: Failed to send the verification code
     */
    private int status;
}
