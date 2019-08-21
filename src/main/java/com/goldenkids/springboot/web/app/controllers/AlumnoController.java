package com.goldenkids.springboot.web.app.controllers;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.goldenkids.springboot.web.app.models.Alumno;
import com.goldenkids.springboot.web.app.models.Inscripcion;
import com.goldenkids.springboot.web.app.models.Usuario;
import com.goldenkids.springboot.web.app.services.AlumnoService;
import com.goldenkids.springboot.web.app.services.InscripcionService;
import com.goldenkids.springboot.web.app.services.PadreService;
import com.goldenkids.springboot.web.app.services.UploadService;

import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/alumno")
public class AlumnoController {

    org.slf4j.Logger log = LoggerFactory.getLogger(AlumnoController.class);

    @Autowired
    private InscripcionService inscripcionService;

    @Autowired
    private AlumnoService alumnoServicio;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private PadreService padreService;

    @GetMapping("/listaralumnos")
    public String listar(@RequestParam(required = false) String q, String error, Model modelo, String msg) {

        List<Alumno> alumnos;
        if (q != null) {
            alumnos = alumnoServicio.buscarAlumnos(q);
        } else {
            alumnos = alumnoServicio.buscarAlumnos();
        }

        for (Alumno alumno : alumnos) {
            Inscripcion inscripcion = inscripcionService.buscarInscripcionActual(alumno);
            if (inscripcion != null) {
                alumno.setSalita(inscripcion.getSalita());
            }
        }

        if (msg != null) {
            switch (msg) {
                case "guardadoOk":
                    modelo.addAttribute("success", "El Alumno ha sido creado con éxito.");
                    break;
                case "modificadoOk":
                    modelo.addAttribute("success", "El Alumno ha sido modificado con éxito.");
                    break;
                case "error":
                    modelo.addAttribute("error", "Ha ocurrido un error con el Alumno.");
                    break;
                default:
                    break;
            }
        }

        modelo.addAttribute("alumnos", alumnos);
        modelo.addAttribute("q", q);
        modelo.addAttribute("pagina", "Alumnos");
        modelo.addAttribute("tituloPagina", "Administración de Alumnos");
        modelo.addAttribute("subtituloPagina", "Utilice este modulo para administrar los registros de Alumnos del jardin.");

        return "alumno-listado";
    }

    @GetMapping("/listaralumnoseliminados")
    public String listarEliminados(Model modelo) {

        modelo.addAttribute("titulo", "Listado de Alumnos Eliminados : ");

        List<Alumno> alumnosEliminados = alumnoServicio.buscarAlumnosEliminados();

        for (Alumno alumno : alumnosEliminados) {
            Inscripcion inscripcion = inscripcionService.buscarInscripcionActual(alumno);
            if (inscripcion != null) {
                alumno.setSalita(inscripcion.getSalita());
            }
        }

        modelo.addAttribute("alumnos", alumnosEliminados);
        modelo.addAttribute("tituloPagina", "Administración de Alumnos");
        modelo.addAttribute("subtituloPagina", "Utilice este modulo para administrar los registros de Alumnos del jardin.");

        return "alumno-listado";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("alumno") @RequestParam Integer dni, String nombre, String apellido,
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaNacimiento, String accion, String selectSalitaId, Integer selectPadreDni, MultipartFile file) {

        String uniqueFileName = uploadService.cargarArchivo(file);//cargo el archivo a la carpeta y devuelvo el nombre del archivo para persistir en la BD

        if (accion.equals("crear")) {
            alumnoServicio.crearAlumno(dni, nombre, apellido, fechaNacimiento, selectPadreDni, uniqueFileName);
            return "redirect:/alumno/listaralumnos?msg=guardadoOk";
        } else if (accion.equals("modificar")) {
            alumnoServicio.modificarAlumno(dni, nombre, apellido, fechaNacimiento, selectPadreDni, uniqueFileName);
            return "redirect:/alumno/listaralumnos?msg=modificadoOk";
        }
        return "redirect:/alumno/listaralumnos?msg=error";
    }

    @GetMapping("/modificar")
    public String modificar(@RequestParam Integer dni, ModelMap model) {

        List<Usuario> padres = padreService.buscarPadres();

        if (dni != null) {
            Alumno alumno = alumnoServicio.buscarAlumno(dni);

            Inscripcion inscripcion = inscripcionService.buscarInscripcionActual(alumno);
            if (inscripcion != null) {
                alumno.setSalita(inscripcion.getSalita());
            }
            model.put("alumno", alumno);
            model.put("accion", "modificar");
            model.put("salitaActual", alumno.getSalita());
            model.put("padres", padres);
        } else {
            model.put("alumno", new Alumno());
            model.put("padres", padres);
            model.put("accion", "crear");
        }

        return "alumno-admin";
    }

    @GetMapping("/formulario")
    public String abrirAlumno(ModelMap modelMap) {

        Alumno alumno = new Alumno();

        List<Usuario> padres = padreService.buscarPadres();

        Inscripcion inscripcion = inscripcionService.buscarInscripcionActual(alumno);

        if (inscripcion != null) {
            alumno.setSalita(inscripcion.getSalita());
        }

        modelMap.put("alumno", alumno);
        modelMap.put("accion", "crear");
        modelMap.put("padres", padres);
        modelMap.put("tituloPagina", "Registro de Alumnos");

        return "alumno-admin";

    }

    @GetMapping("/baja")
    public String darBaja(@RequestParam Integer dni) {

        try {
            alumnoServicio.darDeBaja(dni);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/alumno/listaralumnos";

    }

}
