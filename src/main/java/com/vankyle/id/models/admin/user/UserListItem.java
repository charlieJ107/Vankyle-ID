package com.vankyle.id.models.admin.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserListItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1045712683886244873L;
    private String id;
    private String username;
    private String email;
    private String name;
}
