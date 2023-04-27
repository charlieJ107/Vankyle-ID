package com.vankyle.id.models.authorization;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AuthorizationErrorResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 2454081258545356679L;
    private int status;
    private String message;
}
