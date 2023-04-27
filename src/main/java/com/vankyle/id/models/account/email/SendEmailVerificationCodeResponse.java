package com.vankyle.id.models.account.email;

import lombok.Data;

import java.io.Serial;

@Data
public class SendEmailVerificationCodeResponse implements java.io.Serializable{
    @Serial
    private static final long serialVersionUID = 3962962801055667919L;
    private int status;
}
