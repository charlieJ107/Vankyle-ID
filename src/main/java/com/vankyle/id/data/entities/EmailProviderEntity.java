package com.vankyle.id.data.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "email_providers")
public class EmailProviderEntity {
    @Id
    private Long id;
    private String name;

    private EmailProviderType type;
    // SMTP
    private String host;
    private int port;
    private String username;
    private String password;
    private boolean useSSL;
    private boolean useTLS;
    private String fromAddress;
    private String fromName;
    private String replyToAddress;

}

