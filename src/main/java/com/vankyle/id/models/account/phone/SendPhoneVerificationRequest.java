package com.vankyle.id.models.account.phone;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class SendPhoneVerificationRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 8769007121181659849L;
    private String phone;
    private String locale;
}