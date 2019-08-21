package com.goldenkids.springboot.web.app.controllers;

import com.goldenkids.springboot.web.app.models.Alumno;
import com.goldenkids.springboot.web.app.models.Inscripcion;
import com.goldenkids.springboot.web.app.services.ActividadService;

import com.goldenkids.springboot.web.app.services.AlumnoService;
import com.goldenkids.springboot.web.app.services.InscripcionService;
import java.text.ParseException;

import java.util.List;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("actividades")
public class DirectivoController {

    org.slf4j.Logger log = LoggerFactory.getLogger(DirectivoController.class);

    @Autowired
    private AlumnoService alumnoService;

    @Autowired
    private ActividadService actividadService;

    @Autowired
    private InscripcionService inscripcionService;

    @GetMapping("/directivo")
    public String bienvenida(Model modelo, Authentication authenticated, String q) {
        List<Alumno> hijos;

        if (q != null) {
            hijos = alumnoService.buscarAlumnos(q);
        } else {
            hijos = alumnoService.buscarAlumnos();
        }

        for (Alumno hijo : hijos) {
            Inscripcion inscripcion = inscripcionService.buscarInscripcionActual(hijo);
            if (inscripcion != null) {
                hijo.setSalita(inscripcion.getSalita());
            }
        }

        modelo.addAttribute("hijos", hijos);
        modelo.addAttribute("tituloPagina", "Informacion relativa a los alumnos");

        return "padre-vista";
    }

    @GetMapping("directivo/cargar")
    public String cargarActividades(Model modelo, @RequestParam(required = false) String q, @RequestParam(required = false) String registrado) throws ParseException {

        List<Alumno> alumnos = null;
        if (q != null) {
            alumnos = alumnoService.buscarAlumnos(q);
        } else {
            alumnos = alumnoService.buscarAlumnos();
        }

        if (registrado != null) {
            modelo.addAttribute("success", "La Actividad fue registrada exitosamente");
        }
        
        for (Alumno alumno : alumnos) {
            Inscripcion inscripcion = inscripcionService.buscarInscripcionActual(alumno);
            if (inscripcion != null) {
                alumno.setSalita(inscripcion.getSalita());
            }else{
                alumno.setSalita(null);
            }
        }


        for (Alumno alumno : alumnos) {//valido si esta en el jardin y si esta durmiendo y lo guardo en campo transient de la entidad
            if ((actividadService.estaEnClase(alumno)) == null) {
                log.info("La consulta dice que NO esta en clase");
                alumno.setEnClase(false);
            } else {
                log.info("La consulta dice que esta en clase");
                alumno.setEnClase(true);
            }
            if ((actividadService.estaDurmiendo(alumno)) == null) {
                log.info("La consulta dice que NO esta durmiendo");
                alumno.setDurmiendo(false);
            } else {
                log.info("La consulta dice que esta en durmiendo");
                alumno.setDurmiendo(true);
            }
        }

        modelo.addAttribute("alumnos", alumnos);
        modelo.addAttribute("tituloPagina", "Cargar Actividades a los alumnos");

        return "actividades-sala";
    }

}
