package com.vankyle.id.config;

import com.vankyle.id.config.properties.EmailProperties;
import com.vankyle.id.service.email.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
@EnableConfigurationProperties(EmailProperties.class)
public class EmailConfig {
    @Bean
    public EmailSender emailSender(Environment environment, JavaMailSender javaMailSender) {
        if (environment.getProperty("vankyle.id.mail.enabled", Boolean.class, false)) {
            var sender = new SmtpEmailSender(javaMailSender);
            sender.setFrom(environment.getProperty("vankyle.id.mail.from"));
            return sender;
        }
        return new NoOpEmailSender();
    }


    @Bean
    public EmailTemplateService thymeleafEmailTemplateService(
            TemplateEngine templateEngine,
            Environment environment) {
        return new ThymeleafEmailTemplateService(templateEngine, environment);
    }
}
