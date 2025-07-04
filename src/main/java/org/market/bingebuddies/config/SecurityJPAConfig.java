package org.market.bingebuddies.config;

import org.market.bingebuddies.services.security.JPAUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile({"mysql", "h2"})
public class SecurityJPAConfig {

    private final JPAUserDetailsService userDetailsService;

    public SecurityJPAConfig(JPAUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/movies/new").hasRole("ADMIN")
                        .requestMatchers("/movies/edit/**").hasRole("ADMIN")
                        .requestMatchers("/movies/delete/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/clubs/{clubId}/events/new").authenticated()
                        .requestMatchers(
                                "/h2-console/**",
                                "/webjars/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/resources/**",
                                "/login",
                                "/",
                                "/clubs/**",
                                "/clubs/{clubId}/events/new",
                                "/register",
                                "/movies/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .userDetailsService(userDetailsService)
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/login")
                                .permitAll()
                                .loginProcessingUrl("/perform_login")
                                .defaultSuccessUrl("/clubs", true)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/clubs")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                )
                .exceptionHandling(ex -> ex.accessDeniedPage("/access_denied"))
                .httpBasic(Customizer.withDefaults());

        http.csrf(csrf -> csrf.disable());
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}
