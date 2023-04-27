package com.vankyle.id.models.account.phone;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UpdatePhoneRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -8949937117224177675L;
    private String phone;
    private String code;
}
