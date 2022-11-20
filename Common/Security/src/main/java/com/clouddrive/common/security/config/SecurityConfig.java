package com.clouddrive.common.security.config;

import com.clouddrive.common.security.authentication.TokenAuthenticationProvider;
import com.clouddrive.common.security.filter.TokenAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
//@AutoConfigureBefore(SecurityAutoConfiguration.class)
//@EnableConfigurationProperties({SecurityProperties.class})
//@Import({SecurityDataConfiguration.class})
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(getTokenAuthenticationProvider());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
//                .antMatchers("/login", "/register", "/api/login", "/api/Register", "/css/**", "/images/**", "/js/**").permitAll()
                .antMatchers("/login", "/register").permitAll()
                .anyRequest().authenticated()
//                .and()
//                .formLogin().loginPage("/login").permitAll()
                .and()
                .logout().permitAll();

        http.addFilterBefore(getTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
//        http.addFilterBefore(getAnonymousAuthenticationFilter(), AnonymousAuthenticationFilter.class);
//        http.addFilterBefore()
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
