package com.vankyle.id.models.account.email;

import lombok.Data;

import java.io.Serial;

@Data
public class SendEmailVerificationCodeRequest implements java.io.Serializable{

    @Serial
    private static final long serialVersionUID = -7862053869750842864L;
    private String email;
    private String locale;
}
