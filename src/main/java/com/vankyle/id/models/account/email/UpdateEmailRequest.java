package com.vankyle.id.models.account.email;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UpdateEmailRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 7599142199629992878L;
    private String email;
    private String code;
}
