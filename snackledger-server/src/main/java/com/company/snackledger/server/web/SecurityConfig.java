package com.company.snackledger.server.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain filter(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/kiosk").hasAnyRole("KIOSK", "ADMIN")
                        .requestMatchers("/dashboard/tv").hasAnyRole("KIOSK", "ADMIN")
                        .requestMatchers("/api/v1/kiosk/**").permitAll()
                        .anyRequest().hasRole("ADMIN"))
                .formLogin(form -> form.defaultSuccessUrl("/kiosk", true).permitAll())
                .logout(logout -> logout.permitAll())
                .build();
    }

    @Bean
    UserDetailsService users(PasswordEncoder passwordEncoder) {
        var kiosk = User.withUsername("kiosk")
                .password(passwordEncoder.encode("kiosk-change-me"))
                .roles("KIOSK")
                .build();
        var admin = User.withUsername("admin")
                .password(passwordEncoder.encode("admin-change-me"))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(kiosk, admin);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
