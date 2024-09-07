package com.vankyle.id.models;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Data
public class ConsentResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 6232149857641001866L;
    private int status;
    private Client client;
    private User user;
    private String state;
    private Set<Scope> previouslyApprovedScopes;
    private Set<Scope> scopesToApprove;

    @Data
    public static class User implements Serializable {
        @Serial
        private static final long serialVersionUID = -1635037955466027850L;
        private String principal;
        private String picture;
    }

    @Data
    public static class Client implements Serializable {
        @Serial
        private static final long serialVersionUID = -1542553660351772L;
        private String client_id;
        private String client_name;
        private String client_description;
        private String logo_uri;
    }
    @Data
    public static class Scope  {
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

}
