package com.vankyle.id.models.account.email;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UpdateEmailResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 2418986890494420223L;
    private int status;

}
