package com.vankyle.id.service.validation;

import lombok.Data;

@Data
public class CodeVerification {
    public CodeVerification(boolean valid, String username) {
        this.valid = valid;
        this.username = username;
    }

    private boolean valid;
    private String username;
}
