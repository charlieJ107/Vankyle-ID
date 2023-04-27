package com.vankyle.id.models.account.phone;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class SendPhoneVerificationResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 571737341498404160L;
    private int status;
}
