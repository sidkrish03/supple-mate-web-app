package com.supplemateservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
public class SuppleMateWebSecurityConfig extends WebSecurityConfigurerAdapter{

    // Get rid of explicit constructor and use factory method once switched to Java 11
    private Set<String> allowedHttpMethods;

    public SuppleMateWebSecurityConfig() {
        String[] httpMethods = {"DELETE", "GET", "HEAD", "OPTIONS", "PATCH", "POST", "PUT", "CONNECT"};
        this.allowedHttpMethods = Arrays.stream(httpMethods).collect(Collectors.toSet());
    }

    @Autowired
    UserDetailsService userDetails;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/admin").hasRole("ADMIN")
                .antMatchers("/", "/home", "/signup", "/logout", "/adduser", "/api/*").permitAll()
                .antMatchers("/css/**", "/js/**", "/fonts/**", "/assets/**").permitAll()
                .anyRequest().hasRole("USER")
                .and()
                .formLogin()
                .loginPage("/login")
                .failureUrl("/login?login_error=1")
                .permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/")
                .permitAll();
    }

    @Autowired
    public void configureGlobalInDB(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(userDetails).passwordEncoder(bCryptPasswordEncoder());
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    private StrictHttpFirewall getHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowedHttpMethods(allowedHttpMethods);
        return firewall;
    }
}
