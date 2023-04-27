package com.vankyle.id.config;

import com.vankyle.id.config.handlers.RestfulLogoutSuccessHandler;
import com.vankyle.id.config.handlers.SecurityAuthenticationFailureHandler;
import com.vankyle.id.config.handlers.SecurityHandler;
import com.vankyle.id.data.repository.UserRepository;
import com.vankyle.id.service.security.JpaUserManager;
import com.vankyle.id.service.security.User;
import com.vankyle.id.service.security.UserManager;
import com.vankyle.id.service.security.UsernameAlreadyExistsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final Log logger = LogFactory.getLog(SecurityConfig.class);

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize ->
                        authorize.requestMatchers("/api/admin/*").hasRole("admin")
                                .requestMatchers(
                                        "/static/**", "/favicon.ico",
                                        "/*.json", "/*.ico", "/*.png", "/*.txt",
                                        "/login",
                                        "/api/login",
                                        "/api/consent",
                                        "/api/register",
                                        "/api/confirm-email",
                                        "/api/reset-password",
                                        "/api/forgot-password",
                                        "/error"
                                ).permitAll()
                                .requestMatchers(
                                        "/", "/login", "/register",
                                        "/confirm-email", "/reset-password", "/forgot-password",
                                        "/oidc",
                                        "/account/**",
                                        "/404"
                                ).permitAll()
                                .anyRequest().authenticated())
                .formLogin().loginPage("/login").loginProcessingUrl("/api/login")
                .successHandler(new SecurityHandler())
                .failureHandler(new SecurityAuthenticationFailureHandler())
                .and().logout().logoutUrl("/api/logout").logoutSuccessHandler(new RestfulLogoutSuccessHandler())
                .and().exceptionHandling()
                .accessDeniedHandler(new SecurityHandler());
        http.logout().and().rememberMe();
        http.csrf().disable();
        http.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
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
