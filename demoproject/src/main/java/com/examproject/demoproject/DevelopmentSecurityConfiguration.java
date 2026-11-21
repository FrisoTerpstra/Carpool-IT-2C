package com.examproject.demoproject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// Spring reads this class to find application configuration.
@Configuration
// Open access is enabled only for dev, and excluded if prod is also active.
@Profile("dev & !prod")
public class DevelopmentSecurityConfiguration
{
    // Spring calls this method and uses its result to check incoming requests.
    // Spring supplies HttpSecurity; we do not create it with new ourselves.
    @Bean
    public SecurityFilterChain developmentSecurity(HttpSecurity http) throws Exception
    {
        // Spring's API uses lambdas: each block configures one part.
        http.authorizeHttpRequests(requests ->
        {
            requests.anyRequest().permitAll();
        });

        // Disable the generated login page and the browser's credentials prompt.
        http.formLogin(formLogin ->
        {
            formLogin.disable();
        });

        http.httpBasic(httpBasic ->
        {
            httpBasic.disable();
        });

        // CSRF protection stays enabled. POST requests still need a CSRF token.
        return http.build();
    }
}