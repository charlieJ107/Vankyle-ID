package com.vankyle.id.models.login;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class RedirectResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -513213183497711091L;
    private int status;
    private String redirectUrl;
}
