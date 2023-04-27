package com.vankyle.id.models.account.name;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ChangeInfoRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 7208786431113820961L;
    private String name;
}
