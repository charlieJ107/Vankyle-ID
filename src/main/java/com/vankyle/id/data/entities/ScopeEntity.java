package com.vankyle.id.data.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Entity(name = "scopes")
public class ScopeEntity implements Serializable{
    @Serial
    private static final long serialVersionUID = -1668330103401039827L;

    @Id
    @GeneratedValue
    private Long id;
    private String scopeId;
    private String scopeName;
    private String displayName;
    private String description;
    private String icon;
}
