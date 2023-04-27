package com.vankyle.id.models.account.phone;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UpdatePhoneResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -8223016988290233463L;
    private int status;
}
