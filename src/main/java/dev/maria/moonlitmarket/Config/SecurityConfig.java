package dev.maria.moonlitmarket.Config;

import static org.springframework.security.config.Customizer.*;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    DataSource datasource;
    
    @SuppressWarnings("deprecation")
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests((requests)-> requests
            .requestMatchers("/api/public/**").permitAll()
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
            .httpBasic(withDefaults())
            .csrf(crsf -> crsf.disable());
            
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user1 = User.withUsername("user1")
                .password("{noop}password1").password(passwordEncoder().encode("password1"))
                .roles("USERS")
                .build();

        UserDetails admin = User.withUsername("admin")
                .password("{noop}password2").password(passwordEncoder().encode("password2"))
                .roles("ADMIN")
                .build();

                JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
                userDetailsManager.createUser(user1);
                userDetailsManager.createUser(admin);
                return userDetailsManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
