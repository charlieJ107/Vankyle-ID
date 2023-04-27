package com.vankyle.id.models.account.password;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ChangePasswordResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 2330179051143385559L;
    private int status;
}
