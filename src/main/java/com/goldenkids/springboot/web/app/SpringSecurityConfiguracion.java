/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldenkids.springboot.web.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.goldenkids.springboot.web.app.services.JpaUserSecurityService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author lisandroscofano
 */
@Configuration
public class SpringSecurityConfiguracion extends WebSecurityConfigurerAdapter {

    @Autowired
    private JpaUserSecurityService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/js/**", "/css/**", "/images/**", "/inicio").permitAll()
                .antMatchers("/uploads/**").hasAnyAuthority("DIRECTIVO", "PADRE", "DOCENTE")
                .antMatchers("/usuario/**").hasAuthority("DIRECTIVO")
                .antMatchers("/alumno/**").hasAuthority("DIRECTIVO")
                .antMatchers("/salita/**").hasAuthority("DIRECTIVO")
                .antMatchers("/autorizados/**").hasAuthority("DIRECTIVO")
                .antMatchers("/inscripciones/**").hasAuthority("DIRECTIVO")
                .antMatchers("/salita/**").hasAuthority("DIRECTIVO")
                .antMatchers("/actividades/directivo/**").hasAuthority("DIRECTIVO")
                .antMatchers("/actividades/padre/**").hasAuthority("PADRE")
                .antMatchers("/actividades/detalle").hasAnyAuthority("DIRECTIVO", "PADRE", "DOCENTE")
                .antMatchers("/actividades/tipousuario").hasAnyAuthority("DIRECTIVO", "PADRE", "DOCENTE")
                .antMatchers("/actividades/docente/**").hasAuthority("DOCENTE")
                .antMatchers("/actividades/registraactividad").hasAnyAuthority("DIRECTIVO", "DOCENTE")
                .antMatchers("/actividades/registraactividad/observacion").hasAnyAuthority("DIRECTIVO", "DOCENTE")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login").permitAll()
                .and()
                .logout().permitAll()
                .logoutSuccessUrl("/login")
                .and()
                .exceptionHandling().accessDeniedPage("/error_403");

    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configurerGlobal(AuthenticationManagerBuilder builder) throws Exception {

        builder.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());

        PasswordEncoder encoder = passwordEncoder();

        UserBuilder users = User.builder().passwordEncoder(encoder::encode);

        builder.inMemoryAuthentication()
                .withUser(users.username("golden").password("kids2019").authorities("DIRECTIVO"));

    }
}
