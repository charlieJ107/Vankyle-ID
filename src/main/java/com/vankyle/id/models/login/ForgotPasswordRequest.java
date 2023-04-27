package com.vankyle.id.models.login;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ForgotPasswordRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 4360258989471100195L;
    private String email;
    private String username;
    private String locale;
}
