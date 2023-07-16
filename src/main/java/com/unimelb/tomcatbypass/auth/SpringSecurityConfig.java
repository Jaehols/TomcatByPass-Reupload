package com.unimelb.tomcatbypass.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
    static final String SECRET = "";
    static final Integer SALT_LENGTH = 8;
    static final Integer NUM_ITERATIONS = 1850;
    static final Integer HASH_WIDTH = 256;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(new UserDetailServiceImpl()).passwordEncoder((new Pbkdf2PasswordEncoder(SECRET, SALT_LENGTH, NUM_ITERATIONS, HASH_WIDTH)));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/auth/admin/**")
                .hasRole("ADMIN")
                .antMatchers("/auth/**")
                .authenticated()
                .and()
                .formLogin()
                .defaultSuccessUrl("/auth/login-control", true)
                .and()
                .logout()
                .logoutSuccessUrl("/index.jsp");
    }
}
