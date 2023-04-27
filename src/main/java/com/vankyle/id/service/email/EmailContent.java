package com.vankyle.id.service.email;

import lombok.Data;

@Data
public class EmailContent {
    public EmailContent(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }
    private String subject;
    private String body;
}
