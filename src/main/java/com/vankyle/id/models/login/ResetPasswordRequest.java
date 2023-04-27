package com.vankyle.id.models.login;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String code;
    private String password;
}
