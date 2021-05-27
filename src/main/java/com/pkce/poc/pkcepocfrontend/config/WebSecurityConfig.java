package com.pkce.poc.pkcepocfrontend.config;


import com.pkce.poc.pkcepocfrontend.controller.CustomLogoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomLogoutHandler logoutHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests() //
                //.requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                //.permitAll()
                .antMatchers("/fragments*", "/account", "/home*", "/setup*", "/create-client*").authenticated()
//                .antMatchers("/css/**", "/index").permitAll()
                //.antMatchers("/index").permitAll()
                .antMatchers("/user/**").hasRole("USER")
                .antMatchers("/relogin").permitAll()
                .and().oauth2Client() //
                .and().oauth2Login()
                .and()
                    .logout()
                        .logoutUrl("/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                        .permitAll();

    }

}
