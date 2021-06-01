package com.pkce.poc.pkcepocfrontend.config;


import com.pkce.poc.pkcepocfrontend.controller.CustomLogoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@EnableWebSecurity (debug = false)
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomLogoutHandler logoutHandler;

    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("pwaldorf").password(passwordEncoder().encode("123456")).roles("USER")
                .and()
                .withUser("pwaldorf2").password(passwordEncoder().encode("123456")).roles("USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/relogin").permitAll()
                .antMatchers("/**").authenticated()
                .and()
                .logout()
                .logoutUrl("/logout")
                .deleteCookies("JSESSIONID")
                //.addLogoutHandler(logoutHandler) // used for oauth login
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                .permitAll()
                .and()
                //.oauth2Login();
                .formLogin()
                .defaultSuccessUrl("/", true);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
