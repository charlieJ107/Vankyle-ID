package com.vankyle.id.models.admin.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserItemResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 2130496613135782814L;
    // 2000: success
    // 4001: user already exists
    // 4004: user not found
    private int status;
    private UserItem user;
    private String message;
}
