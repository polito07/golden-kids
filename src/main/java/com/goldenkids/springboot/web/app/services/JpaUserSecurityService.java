/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldenkids.springboot.web.app.services;

import com.goldenkids.springboot.web.app.models.Usuario;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author lisandroscofano
 */
@Service
public class JpaUserSecurityService implements UserDetailsService {

    Logger log = LoggerFactory.getLogger(JpaUserSecurityService.class);

    @Autowired
    private UsuarioService usuarioService;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Usuario usuario = usuarioService.buscarUsuarioPorUserName(username);//agregar filtro con fecha no nula

        if (usuario == null) {
            log.error("Error en el Login: no existe el usuario '" + username + "' en el sistema!");
            throw new UsernameNotFoundException("Username: " + username + " no existe en el sistema!");
        }

        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

        authorities.add(new SimpleGrantedAuthority(usuario.getRol().getPerfil().toString()));
        return new User(usuario.getNombreUsuario(), usuario.getPassword(), true, true, true, true, authorities);
    }

    public String nombreApellidoUsuarioLogueado(Authentication userAuth) {
        String userName = userAuth.getName();
        Usuario usuarioLogueado = usuarioService.buscarUsuarioPorUserName(userName);
        String UserLog = null;
        if (usuarioLogueado != null) {
            UserLog = usuarioLogueado.getNombre() + " " + usuarioLogueado.getApellido();
        } else {
            UserLog = "Super Admin Golden";
        }
        return UserLog;
    }

}
