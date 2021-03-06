/*
 * Copyright (c) 2015 Espark And ©Adarsh Development Services @copyright All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Espark nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.espark.adarsh.configuration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


/**
 * @author Adarsh Kumar
 * @author $LastChangedBy: Adarsh Kumar$
 * @version $Revision: 0001 $, $Date:: 1/1/10 0:00 AM#$
 * @Espark @copyright all right reserve
 */
@Configuration
@EnableWebSecurity
@PropertySource("classpath:/application.properties")
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${espark.sadmin.user.name}")
    private String superAdminName;
    @Value("${espark.sadmin.user.pwd}")
    private String superAdminPwd;
    @Value("${espark.sadmin.user.role}")
    private String superAdminRole;

    @Value("${espark.admin.user.name}")
    private String adminName;
    @Value("${espark.admin.user.pwd}")
    private String adminPwd;
    @Value("${espark.admin.user.role}")
    private String adminRole;

    @Value("${espark.user.name}")
    private String userName;
    @Value("${espark.user.pwd}")
    private String userPwd;
    @Value("${espark.user.role}")
    private String userRole;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser(userName).password(userPwd).roles(userRole);
        auth.inMemoryAuthentication().withUser(adminName).password(adminPwd).roles(adminRole);
        auth.inMemoryAuthentication().withUser(superAdminName).password(superAdminPwd).roles(superAdminRole);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/static/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/static/css/**").access("permitAll")
                .antMatchers("/static/js/**").access("permitAll")
                .antMatchers("/login**").access("permitAll")
                .antMatchers("/logout**").access("permitAll")
                .antMatchers("/user/**").access("hasRole('ROLE_USER')or hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
                .antMatchers("/sadmin/**").access("hasRole('ROLE_SUPER_ADMIN')")
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login")
                .defaultSuccessUrl("/welcome")
                .failureUrl("/login?error")
                .usernameParameter("username").passwordParameter("password")
                .and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/login?logout").invalidateHttpSession(true)
                .clearAuthentication(true)
                .and()
                .exceptionHandling().accessDeniedPage("/accessDenied");

    }
}