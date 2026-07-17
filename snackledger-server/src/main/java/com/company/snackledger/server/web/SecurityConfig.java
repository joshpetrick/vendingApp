package com.company.snackledger.server.web;

import com.company.snackledger.server.config.DemoUsers;
import java.util.ArrayList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain filter(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/images/**", "/favicon.ico").permitAll()
                        .requestMatchers("/kiosk").hasAnyRole("KIOSK", "ADMIN")
                        .requestMatchers("/dashboard/tv").hasAnyRole("KIOSK", "ADMIN")
                        .requestMatchers("/api/v1/kiosk/**").permitAll()
                        .anyRequest().hasRole("ADMIN"))
                .formLogin(form -> form.successHandler((request, response, authentication) -> {
                    boolean admin = authentication.getAuthorities().stream()
                            .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
                    response.sendRedirect(admin ? "/admin" : "/kiosk");
                }).permitAll())
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .build();
    }

    @Bean
    UserDetailsService users(PasswordEncoder passwordEncoder) {
        var users = new ArrayList<UserDetails>();
        users.add(User.withUsername("kiosk")
                .password(passwordEncoder.encode("kiosk-change-me"))
                .roles("KIOSK")
                .build());
        users.add(User.withUsername("admin")
                .password(passwordEncoder.encode("admin-change-me"))
                .roles("ADMIN")
                .build());

        DemoUsers.NAMES.forEach(name -> users.add(User.withUsername(name)
                .password(passwordEncoder.encode(name))
                .roles("USER")
                .build()));

        return new InMemoryUserDetailsManager(users);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
