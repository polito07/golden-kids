package com.goldenkids.springboot.web.app.controllers;

import com.goldenkids.springboot.web.app.models.Actividad;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.goldenkids.springboot.web.app.models.Alumno;
import com.goldenkids.springboot.web.app.models.Docente;
import com.goldenkids.springboot.web.app.models.Salita;
import com.goldenkids.springboot.web.app.models.TipoActividad;
import com.goldenkids.springboot.web.app.models.TipoCantidad;
import com.goldenkids.springboot.web.app.models.TipoPanial;
import com.goldenkids.springboot.web.app.models.Usuario;
import com.goldenkids.springboot.web.app.services.ActividadService;
import com.goldenkids.springboot.web.app.services.AlumnoService;
import com.goldenkids.springboot.web.app.services.DocenteService;
import com.goldenkids.springboot.web.app.services.UsuarioService;
import java.text.ParseException;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/actividades")
public class ActividadesController {

    @Autowired
    private ActividadService actividadService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private DocenteService docenteService;

    @Autowired
    private AlumnoService alumnoService;

    Logger log = LoggerFactory.getLogger(ActividadesController.class);

    @GetMapping("/tipousuario")
    public String redireccionaSegunUsuario(Authentication authenticated) {
        String tipoUsuario = authenticated.getAuthorities().toString();
        log.info("El tipo de usuario logueado para ver actividades es: " + tipoUsuario);

        if (tipoUsuario.equals("[DIRECTIVO]")) {
            return "redirect:/actividades/directivo";
        }
        if (tipoUsuario.equals("[PADRE]")) {
            return "redirect:/actividades/padre";
        }
        if (tipoUsuario.equals("[DOCENTE]")) {
            return "redirect:/actividades/docente/plantilla";
        }
        return null;
    }

    @GetMapping("/detalle")
    public String verDetalle(@RequestParam(required = true) Integer dni, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaSolicitada, Model modelo, Authentication authenticated) throws ParseException {

        Date fecha = null;
        if (fechaSolicitada != null) {
            log.info("La fecha pasada en String es: " + fechaSolicitada);
            fecha = fechaSolicitada;
        } else {
            fecha = new Date();
        }
        fecha = actividadService.fechaFormateadaParaJpql(fecha);
        Date diaPosterior = actividadService.diaPosteariorFormateadoParaJpql(fecha);

        Alumno alumno = alumnoService.buscarAlumno(dni);

        List<Actividad> actividades = actividadService.buscarActividadesPorAlumno(alumno, fecha, diaPosterior);

        modelo.addAttribute("actividades", actividades);
        modelo.addAttribute("tituloPagina", "Informacion relativa a " + alumno.getNombre() + " " + alumno.getApellido());
        modelo.addAttribute("dni", dni);

        return "detalle-alumno";
    }

    @GetMapping("/docente/plantilla")
    public String plantilla(Authentication authenticated, Model modelo, @RequestParam(required = false) String registrado) throws ParseException {

        Usuario usuario = usuarioService.buscarUsuarioPorUserName(authenticated.getName());

        Docente docente = docenteService.buscarDocentePorUsuario(usuario);
        Salita salita = docente.getSalita();
        String tipoDocente = docente.getTipoDocente().toString();

        if (tipoDocente.equals("AUXILIAR")) {
            return "error_403";
        }

        if (tipoDocente.equals("REEMPLAZANTE")) {
            return "error_403";
        }

        modelo.addAttribute("titulo", "Listado de alumnos");
        List<Alumno> alumnos;

        alumnos = actividadService.buscarAlumnnosPorSalita(salita);

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

        if (registrado != null) {
            modelo.addAttribute("success", "La Actividad fue registrada exitosamente");
        }

        modelo.addAttribute("nombreSalita", salita.getNombre());
        modelo.addAttribute("alumnos", alumnos);
        modelo.addAttribute("tituloPagina", "Actividades alumnos salita " + salita.getNombre());
        modelo.addAttribute("subtituloPagina", "Utilice este modulo para cargar las Actividades de los alumnos de la salita " + salita.getNombre());

        return "actividades-sala";
    }

    @GetMapping("/registraactividad")
    public String guardarActividad(@RequestParam(required = false) TipoActividad tipoActividad,
            @RequestParam(required = false) Integer cantidadLeche,
            @RequestParam(required = false) TipoCantidad tipoCantidad,
            @RequestParam(required = false) TipoPanial tipoPanial, @RequestParam(required = false) String observacion,
            @RequestParam(required = false) int dni, ModelMap modelMap,
            Authentication authenticated) {
        log.info("El dni del alumno es:" + dni);
        try {
            actividadService.crearActividad(tipoActividad, cantidadLeche, tipoCantidad, tipoPanial, observacion, dni, authenticated);
        } catch (Exception e) {
            e.getMessage();
        }

        log.info("Estado del alumno: " + alumnoService.buscarAlumno(dni).isEnClase());

        String tipoUsuario = authenticated.getAuthorities().toString();

        if (tipoUsuario.equals("[DIRECTIVO]")) {
            return "redirect:/actividades/directivo/cargar?registrado=ok";
        }
        if (tipoUsuario.equals("[DOCENTE]")) {
            return "redirect:/actividades/docente/plantilla?registrado=ok";
        }

        return "redirect:/actividades/tipousuario";
    }

    @PostMapping("/registraactividad/observacion")
    public String guardarActividadObservacion(
            @RequestParam(required = true) String txtObservacion,
            @RequestParam(required = true) Integer dniAlumno,
            ModelMap modelMap,
            Authentication authenticated) {
        log.info("La observacion es:" + txtObservacion);

        try {
            actividadService.crearActividadObservacion(txtObservacion, dniAlumno, authenticated);
        } catch (Exception e) {
            e.getMessage();
        }

        String tipoUsuario = authenticated.getAuthorities().toString();

        if (tipoUsuario.equals("[DIRECTIVO]")) {
            return "redirect:/actividades/directivo/cargar?registrado=ok";
        }
        if (tipoUsuario.equals("[DOCENTE]")) {
            return "redirect:/actividades/docente/plantilla?registrado=ok";
        }

        return "redirect:/actividades/tipousuario";
    }

    @GetMapping("directivo/eliminar")
    public String eliminarActividad(@RequestParam(required = true) String id, @RequestParam(required = true) Integer dniAlumno) {
        actividadService.eliminarActividad(id);
        return "redirect:/actividades/detalle?dni=" + dniAlumno;
    }
}
