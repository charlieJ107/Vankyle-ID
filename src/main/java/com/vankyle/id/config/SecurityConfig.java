package com.vankyle.id.config;

import com.vankyle.id.config.handlers.*;
import com.vankyle.id.data.repository.UserRepository;
import com.vankyle.id.service.security.JpaUserManager;
import com.vankyle.id.service.security.User;
import com.vankyle.id.service.security.UserManager;
import com.vankyle.id.service.security.UsernameAlreadyExistsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final Log logger = LogFactory.getLog(SecurityConfig.class);
    @Value("${vankyle.id.api-path}")
    private String apiPath;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize ->
                        authorize.requestMatchers(
                                        "/static/**", "/favicon.ico",
                                        "/*.json", "/*.ico", "/*.png", "/*.txt",
                                        apiPath + "/login", "/login",
                                        apiPath + "/register", "/register",
                                        apiPath + "/confirm-email", "/confirm-email",
                                        apiPath + "/reset-password", "/reset-password",
                                        apiPath + "/forgot-password", "/forgot-password",
                                        "/error"
                                ).permitAll()
                                .requestMatchers(apiPath + "/admin/**").hasRole("admin")
                                .anyRequest().authenticated())
                .formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer
                        .loginPage("/login")
                        .loginProcessingUrl(apiPath + "/login")
                        .successHandler(new JsonResponseAuthenticationSuccessHandler())
                        .failureHandler(new JsonResponseAuthenticationFailureHandler()))
                .logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
                        .logoutUrl(apiPath + "/logout")
                        .logoutSuccessHandler(new JsonResponseLogoutSuccessHandler()))

                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
                        .accessDeniedHandler(new JsonResponseAccessDeniedHandler())
                        .authenticationEntryPoint(new JsonResponseAuthenticationEntryPoint("/login")))
                .rememberMe(httpSecurityRememberMeConfigurer -> httpSecurityRememberMeConfigurer
                        .rememberMeCookieName("remember-me")
                        .rememberMeParameter("remember-me")
                        .key("remember-me")
                        .tokenValiditySeconds(86400));
        http.csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    UserManager userManager(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        var userManager = new JpaUserManager(userRepository, passwordEncoder);
        try {
            userManager.createUser(User.withUsername("admin")
                    .rawPassword("admin")
                    .roles("admin")
                    .build());
        } catch (UsernameAlreadyExistsException e) {
            logger.warn("Admin user already exists, skipping creation");
        }
        return userManager;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
