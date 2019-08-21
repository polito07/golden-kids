/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldenkids.springboot.web.app.controllers;

import com.goldenkids.springboot.web.app.models.Usuario;
import com.goldenkids.springboot.web.app.services.UsuarioService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author lisandroscofano
 */
@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @RequestMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/inicio")
    public String inicio(Model modelo, Authentication autenticado) {

        Usuario usuario = usuarioService.buscarUsuarioPorUserName(autenticado.getName());

        if (usuario != null) {
            modelo.addAttribute("tituloPagina", "Bienvenido a Golden Kids " + usuario.getNombre() + " " + usuario.getApellido());
        } else {
            modelo.addAttribute("tituloPagina", "Bienvenido a Golden Kids " + autenticado.getName());
        }

        return "inicio.html";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, @RequestParam(value = "logout", required = false) String logout, Model model, Principal principal) {

        if (principal != null) {
            return "redirect:/inicio";//tenemos que tener una direccion raiz distinta del login
        }

        if (error != null) {
            model.addAttribute("error", "Usuario o Contraseña incorrecta");
        }

        if (logout != null) {
            model.addAttribute("success", "Ha cerrado sesion con éxito");
        }

        return "login";
    }

}
