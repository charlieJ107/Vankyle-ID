package com.vankyle.id.service.email;

import com.vankyle.id.service.security.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.ResourceBundle;

public class ThymeleafEmailTemplateService implements EmailTemplateService {
    private final TemplateEngine templateEngine;
    private final Environment environment;

    public ThymeleafEmailTemplateService(TemplateEngine templateEngine,
                                         Environment environment) {
        this.templateEngine = templateEngine;
        this.environment = environment;
    }

    @Value("${vankyle.id.name}")
    private String appName;
    @Override
    public EmailContent newConfirmEmail(User user, String code, Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
        Context context = getPublicCodeContext(user, code, bundle);
        var title = bundle.getString("email.verification.title");
        context.setVariable("title", title);
        context.setVariable("greeting", String.format(bundle.getString("email.verification.greeting"), appName));
        return new EmailContent(title,
                templateEngine.process("verification-code", context));
    }

    @Override
    public EmailContent newActivateEmail(User user, String verificationCode, Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
        Context context = getPublicUrlContext(user, verificationCode, bundle, "confirm-email");
        var title = bundle.getString("email.activate.title");
        context.setVariable("title", title);
        context.setVariable("greeting", String.format(bundle.getString("email.activate.greeting"), appName));
        context.setVariable("link_text", bundle.getString("email.activate.link"));
        context.setVariable("if_not", String.format(bundle.getString("email.activate.if_not"), appName));
        return new EmailContent(title,
                templateEngine.process("verification-link", context));
    }


    @Override
    public EmailContent newResetPasswordEmail(User user, String code, Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
        Context context = getPublicUrlContext(user, code, bundle, "reset-password");
        var title = bundle.getString("email.password.reset.title");
        context.setVariable("title", title);
        context.setVariable("greeting", String.format(bundle.getString("email.password.reset.greeting"), appName));
        context.setVariable("link_text", bundle.getString("email.password.reset.link"));
        context.setVariable("if_not", String.format(bundle.getString("email.password.reset.if_not"), appName));
        return new EmailContent(title,
                templateEngine.process("verification-link", context));
    }

    @Override
    public EmailContent newChangePasswordEmail(User user, Locale locale) {
        throw new UnsupportedOperationException();
    }

    private Context getPublicUrlContext(User user, String verificationCode, ResourceBundle bundle, String purpose) {
        Context context = getPublicGreetingContext(user, bundle);
        String url = environment.getProperty("vankyle.id.mail.base-url") +
                String.format("/%s?code=%s", purpose, verificationCode);
        context.setVariable("link_url", url);
        return context;
    }

    private Context getPublicCodeContext(User user, String code, ResourceBundle bundle) {
        Context context = getPublicGreetingContext(user, bundle);
        context.setVariable("code", code);
        return context;
    }

    private Context getPublicGreetingContext(User user, ResourceBundle bundle) {
        Context context = new Context();
        String hi = bundle.getString("email.hi");
        if (user.getName() == null) {
            context.setVariable("hi", String.format(hi, user.getUsername()));
        } else {
            context.setVariable("hi", String.format(hi, user.getName()));
        }
        var baseUrl = environment.getProperty("vankyle.id.mail.base-url");
        context.setVariable("base_url", baseUrl);
        return context;
    }
}
