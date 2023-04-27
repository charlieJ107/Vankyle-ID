package com.vankyle.id.models.register;

import lombok.Data;
@Data
public class RegisterResponse {
    /**
     * 200: success
     * 400: email already exists
     * 500: unknown error
     */
    private int status;
}
