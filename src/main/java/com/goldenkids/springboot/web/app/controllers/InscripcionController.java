package com.goldenkids.springboot.web.app.controllers;

import com.goldenkids.springboot.web.app.models.Alumno;
import com.goldenkids.springboot.web.app.models.Inscripcion;
import com.goldenkids.springboot.web.app.models.Salita;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.goldenkids.springboot.web.app.services.AlumnoService;
import com.goldenkids.springboot.web.app.services.InscripcionService;
import com.goldenkids.springboot.web.app.services.SalitaService;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;

@Controller
@RequestMapping("/inscripciones")
public class InscripcionController {

    @Autowired
    private InscripcionService inscripcionService;

    @Autowired
    private SalitaService salitaService;

    @Autowired
    private AlumnoService alumnoService;

    Logger log = LoggerFactory.getLogger(InscripcionController.class);

    @RequestMapping("/listarinscripciones")
    public String listar(@RequestParam(required = false) String q, Model model, String msg) {

        List<Inscripcion> inscripciones;

        if (q != null) {
            inscripciones = inscripcionService.buscarInscripciones(q);
        } else {
            inscripciones = inscripcionService.buscarInscripciones();
        }

        if (msg != null) {
            switch (msg) {
                case "guardadoOk":
                    model.addAttribute("success", "La Inscripcion ha sido creada con éxito.");
                    break;
                case "modificadoOk":
                    model.addAttribute("success", "La Inscripcion ha sido modificada con éxito.");
                    break;
                case "error":
                    model.addAttribute("error", "Ha ocurrido un error con la Inscripcion.");
                    break;
                case "errorAlumno":
                    model.addAttribute("error", "El alumno seleccionado tiene una inscripcion abierta. Por favor ingresar fecha de baja antes de realizar una nueva inscripcion.");
                    break;
                default:
                    break;
            }
        }

        model.addAttribute("inscripciones", inscripciones);
        model.addAttribute("titulo", "Administracion de Inscripciones");
        model.addAttribute("tituloPagina", "Administración de Inscripciones");
        model.addAttribute("subtituloPagina", "Utilice este modulo para administrar los registros de Inscripciones del jardin.");
        return "inscripcion-listado";
    }

    @RequestMapping("/listarinscripcioneseliminados")
    public String listarEliminados(Model model) {

        List<Inscripcion> inscripcionesEliminados = inscripcionService.buscarInscripcionesEliminadas();

        model.addAttribute("inscripciones", inscripcionesEliminados);
        model.addAttribute("titulo", "Administracion de Inscripciones");
        model.addAttribute("tituloPagina", "Administración de Inscripciones");
        model.addAttribute("subtituloPagina", "Utilice este modulo para administrar los registros de Inscripciones del jardin.");

        return "inscripcion-listado";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam Integer selectAlumnoDni, String selectSalitaId, @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaAlta, @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaBaja, String accion, String inscripcionId, Model model) {

        if (accion.equals("crear")) {
            if (inscripcionService.yaInscripto(selectAlumnoDni)) {
                return "redirect:/inscripciones/listarinscripciones?msg=errorAlumno";
            } else {
                inscripcionService.crearInscripcion(selectAlumnoDni, selectSalitaId, fechaAlta, fechaBaja);
                return "redirect:/inscripciones/listarinscripciones?msg=guardadoOk";
            }
        } else if (accion.equals("modificar")) {
            inscripcionService.modificarInscripcion(selectAlumnoDni, selectSalitaId, fechaAlta, fechaBaja, inscripcionId);
            return "redirect:/inscripciones/listarinscripciones?msg=modificadoOk";
        }
        return "redirect:/inscripciones/listarinscripciones?msg=error";
    }

    @GetMapping("/modificar")
    public String modificar(@RequestParam String id, ModelMap model) {

        List<Salita> salitas = salitaService.buscarSalitas();
        List<Alumno> alumnos = alumnoService.buscarAlumnos();

        if (id != null) {
            Inscripcion inscripcion = inscripcionService.buscarInscripcion(id);
            model.put("inscripcion", inscripcion);
            model.put("accion", "modificar");
            model.put("tituloPagina", "Registro de Inscripciones");
            model.put("salitas", salitas);
            model.put("alumnos", alumnos);
            model.put("salitaActual", inscripcion.getSalita());
            model.put("alumnoActual", inscripcion.getAlumno());
        } else {
            model.put("inscripcion", new Inscripcion());
            model.put("accion", "modificar");
            model.put("tituloPagina", "Registro de Inscripciones");
            model.put("salitas", salitas);
            model.put("alumnos", alumnos);
        }

        return "inscripcion-admin";
    }

    @GetMapping("/formulario")
    public String abrirInscripcion(ModelMap modelMap) {

        Inscripcion inscripcion = new Inscripcion();
        List<Salita> salitas = salitaService.buscarSalitas();
        List<Alumno> alumnos = alumnoService.buscarAlumnos();

        modelMap.put("inscripcion", inscripcion);
        modelMap.put("salitas", salitas);
        modelMap.put("alumnos", alumnos);
        modelMap.put("accion", "crear");
        modelMap.put("tituloPagina", "Registro de Inscripciones");

        return "inscripcion-admin";

    }

}
