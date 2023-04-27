package com.vankyle.id.models.account.name;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ChangeInfoResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 3510179464586251197L;
    private int status;
}
