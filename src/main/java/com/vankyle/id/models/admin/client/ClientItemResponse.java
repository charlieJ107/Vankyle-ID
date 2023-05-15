package com.vankyle.id.models.admin.client;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ClientItemResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 2791734438523319741L;
    private int status;
    private ClientItem client;
    private String message;
}
