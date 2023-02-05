package com.clouddrive.common.security.config;

import com.clouddrive.common.security.authentication.TokenAuthenticationProvider;
import com.clouddrive.common.security.filter.TokenAuthenticationFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

//    @Bean
//    @ConditionalOnMissingBean({AuthenticationManager.class})
//    protected AuthenticationManager authenticationManager(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
//        authenticationManagerBuilder.authenticationProvider(getTokenAuthenticationProvider());
//        return authenticationManagerBuilder.build();
//    }

//    @Bean
//    public void configure(WebSecurity web) throws Exception {
//    }

    @Bean
    @ConditionalOnMissingBean({SecurityFilterChain.class})
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.addFilterBefore(getTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> {
                    auth.antMatchers("/External/Share/Control/**").hasRole("USER")
                            .antMatchers("/Administration/**").hasRole("ADMIN")
                            .antMatchers("/External/login", "/External/register", "/External/mailCode/**", "/External/Share/**", "/System/**").permitAll()
                            .anyRequest().hasRole("USER");
                }).csrf().disable();
        return http.build();
    }

    @Bean
    TokenAuthenticationFilter getTokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }

    @Bean
    AnonymousAuthenticationFilter getAnonymousAuthenticationFilter() {
        return new AnonymousAuthenticationFilter("Anonymous");
    }

    @Bean
    TokenAuthenticationProvider getTokenAuthenticationProvider() {
        return new TokenAuthenticationProvider();
    }
}
