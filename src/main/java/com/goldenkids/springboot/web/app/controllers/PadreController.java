package com.goldenkids.springboot.web.app.controllers;

import com.goldenkids.springboot.web.app.models.Alumno;
import com.goldenkids.springboot.web.app.models.Inscripcion;
import com.goldenkids.springboot.web.app.models.Usuario;
import com.goldenkids.springboot.web.app.services.ActividadService;
import com.goldenkids.springboot.web.app.services.AlumnoService;
import com.goldenkids.springboot.web.app.services.InscripcionService;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.goldenkids.springboot.web.app.services.PadreService;
import com.goldenkids.springboot.web.app.services.UsuarioService;

import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("actividades")
public class PadreController {

    org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private PadreService padreServicio;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private InscripcionService inscripcionService;

    @GetMapping("/padre")
    public String bienvenida(Model modelo, Authentication authenticated) {

        Usuario usuario = usuarioService.buscarUsuarioPorUserName(authenticated.getName());

        List<Alumno> hijos = padreServicio.buscarHijos(usuario);

        for (Alumno hijo : hijos) {
            Inscripcion inscripcion = inscripcionService.buscarInscripcionActual(hijo);
            if (inscripcion != null) {
                hijo.setSalita(inscripcion.getSalita());
            }
        }

        modelo.addAttribute("hijos", hijos);
        modelo.addAttribute("tituloPagina", "Informacion relativa a sus hijos");

        return "padre-vista";
    }

}
