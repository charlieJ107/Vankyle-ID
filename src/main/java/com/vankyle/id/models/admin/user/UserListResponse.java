package com.vankyle.id.models.admin.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;

@Data
public class UserListResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -6554085645651271897L;
    private int status;
    private String message;
    private Collection<UserListItem> users;
}
