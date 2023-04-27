package com.vankyle.id.service.email;


import com.vankyle.id.service.security.User;

import java.util.Locale;

public interface EmailTemplateService {
    EmailContent newResetPasswordEmail(User user, String code, Locale locale);

    EmailContent newConfirmEmail(User user, String code, Locale locale);

    EmailContent newActivateEmail(User user, String code, Locale locale);

    EmailContent newChangePasswordEmail(User user, Locale locale);
}
