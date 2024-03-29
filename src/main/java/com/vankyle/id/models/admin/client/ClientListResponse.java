package com.vankyle.id.models.admin.client;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;

@Data
public class ClientListResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 3883297703163887916L;
    private int status;
    private String message;
    private Collection<ClientItem> clients;
}
