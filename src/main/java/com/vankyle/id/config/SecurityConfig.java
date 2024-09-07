package com.vankyle.id.config;

import com.vankyle.id.data.repositories.UserRepository;
import com.vankyle.id.handlers.LoginHandler;
import com.vankyle.id.services.security.JpaUserDetailsManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;
import java.util.UUID;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        // Permit all on /api/register
                        .requestMatchers("/api/register").permitAll()
                        .anyRequest().authenticated()
                )
                // Form login handles the redirect to the login page from the
                // authorization server filter chain
                .formLogin(httpSecurityFormLoginConfigurer -> {
                    LoginHandler loginHandler = new LoginHandler();
                    httpSecurityFormLoginConfigurer
                            .loginPage("/login")
                            .loginProcessingUrl("/api/login")
                            .failureHandler(loginHandler)
                            .successHandler(loginHandler)
                            .permitAll();
                })
                .csrf(AbstractHttpConfigurer::disable);
        // If debug = true in application.properties, or --debug on the command line is set
        if (Boolean.parseBoolean(System.getProperty("debug", "true"))) {
            http.cors(httpSecurityCorsConfigurer ->
                    httpSecurityCorsConfigurer.configurationSource(request -> {
                        CorsConfiguration corsConfiguration = new CorsConfiguration();
                        corsConfiguration.setAllowedOrigins(List.of("*"));
                        corsConfiguration.setAllowedMethods(List.of("*"));
                        corsConfiguration.setAllowedHeaders(List.of("*"));
                        return corsConfiguration;
                    })
            );
        }

        return http.build();
    }

    @Bean
    public UserDetailsManager userDetailsManager(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        UserDetailsManager userManager = new JpaUserDetailsManager(userRepository, passwordEncoder);

        if (!userManager.userExists("admin")) {

            String randomPassword = UUID.randomUUID().toString().replace("-", "");
            if (Boolean.parseBoolean(System.getProperty("debug", "true"))){
                randomPassword = "password";
            }
            userManager.createUser(User.withUsername("admin")
                    // Random password for admin user
                    .password(randomPassword)
                    .passwordEncoder(passwordEncoder::encode)
                    .roles("USER", "ADMIN")
                    .disabled(false)
                    .build());
            logger.info("Created admin user with password: " + randomPassword);
        } else {
            logger.info("Admin user already exists");
        }
        return userManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
