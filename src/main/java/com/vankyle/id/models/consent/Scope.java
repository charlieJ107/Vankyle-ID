package com.vankyle.id.models.consent;

import lombok.Data;

@Data
public class Scope  {
    public Scope(String scope_id, String scope_name, String scope_description, boolean scope_approved) {
        this.scope_id = scope_id;
        this.scope_name = scope_name;
        this.scope_description = scope_description;
        this.approved = scope_approved;
    }

    private String scope_id;
    private String scope_name;
    private String scope_description;
    private boolean approved;
}
