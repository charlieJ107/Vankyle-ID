package com.vankyle.id.models.account.password;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ChangePasswordRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 7854744616424111256L;
    private String currentPassword;
    private String newPassword;
}
