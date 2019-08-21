package com.goldenkids.springboot.web.app.controllers;

import com.goldenkids.springboot.web.app.models.Docente;
import com.goldenkids.springboot.web.app.models.Rol;
import com.goldenkids.springboot.web.app.models.Salita;
import com.goldenkids.springboot.web.app.models.TipoDocente;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.goldenkids.springboot.web.app.models.TipoPerfil;
import com.goldenkids.springboot.web.app.models.Usuario;
import com.goldenkids.springboot.web.app.services.DocenteService;
import com.goldenkids.springboot.web.app.services.RolService;
import com.goldenkids.springboot.web.app.services.SalitaService;
import com.goldenkids.springboot.web.app.services.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioServicio;

    @Autowired
    private DocenteService docenteService;

    @Autowired
    private SalitaService salitaService;

    @Autowired
    private RolService rolService;

    Logger log = LoggerFactory.getLogger(UsuarioController.class);

    @GetMapping("/listarusuarios")
    public String listar(@RequestParam(required = false) String q, Model modelo, Authentication authentication, String msg) {

        log.info(TipoPerfil.DIRECTIVO.toString());

        List<Usuario> usuarios;
        if (q != null) {
            usuarios = usuarioServicio.buscarUsuarios(q);
        } else {
            usuarios = usuarioServicio.buscarUsuarios();
        }

        if (msg != null) {
            switch (msg) {
                case "guardadoOk":
                    modelo.addAttribute("success", "El Usuario ha sido creado con éxito.");
                    break;
                case "modificadoOk":
                    modelo.addAttribute("success", "El Usuario ha sido modificado con éxito.");
                    break;
                case "error":
                    modelo.addAttribute("error", "Ha ocurrido un error con el Usuario.");
                    break;
                case "errorUsuario":
                    modelo.addAttribute("error", "El usuario ingresado ya existe. Por favor revise el DNI.");
                    break;
                default:
                    break;
            }
        }

        log.info("El Nombre del usuario logueado es: " + authentication.getName() + " y su ROL es : " + authentication.getPrincipal().toString());
        modelo.addAttribute("q", q);
        modelo.addAttribute("usuarios", usuarios);
        modelo.addAttribute("titulo", "Administracion de Usuarios");
        modelo.addAttribute("tituloPagina", "Administración de Usuarios");
        modelo.addAttribute("subtituloPagina", "Utilice este modulo para administrar los registros de Usuarios del jardin.");

        return "usuario-listado";
    }

    @GetMapping("/listarusuarioseliminados")
    public String listarEliminados(Model modelo) {

        modelo.addAttribute("titulo", "Listado de Usuarios: ");

        List<Usuario> usuariosEliminados = usuarioServicio.buscarUsuariosEliminados();

        modelo.addAttribute("usuarios", usuariosEliminados);
        modelo.addAttribute("titulo", "Administracion de Usuarios");
        modelo.addAttribute("tituloPagina", "Administración de Usuarios");
        modelo.addAttribute("subtituloPagina", "Utilice este modulo para administrar los registros de Usuarios del jardin.");

        return "usuario-listado";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("usuario") @RequestParam String nombre, String apellido, String password,
            String mail, String telefono, String nombreUsuario, int dni, TipoPerfil tipoPerfil, String accion, String selectSalitaId, TipoDocente selectTipoDocente) {
        if (accion.equals("crear")) {
            if (usuarioServicio.buscarUsuario(dni) == null) {
                usuarioServicio.crearUsuario(nombre, apellido, password, mail, telefono, nombreUsuario, dni, tipoPerfil);
                if (tipoPerfil.toString().equals("DOCENTE")) {
                    docenteService.crearDocente(usuarioServicio.buscarUsuario(dni), selectSalitaId, selectTipoDocente);
                }
                return "redirect:/usuario/listarusuarios?msg=guardadoOk";
            } else {
                log.error("Ya existe un usuario ingresado con ese DNI");
                return "redirect:/usuario/listarusuarios?msg=errorUsuario";
            }
        } else if (accion.equals("modificar")) {
            Usuario usuarioOriginal = usuarioServicio.buscarUsuario(dni);
            String rolOriginal = usuarioOriginal.getRol().getPerfil().toString();

            log.info("El Perfil original es: " + rolOriginal);
            log.info("El nuevo perfil es: " + tipoPerfil.toString());

            if (((rolOriginal.equals("PADRE")) || (rolOriginal.equals("DIRECTIVO"))) && (tipoPerfil.toString().equals("DOCENTE"))) {//si cambio de padre o directivo a docente
                docenteService.modificarNoDocenteADocente(usuarioOriginal, selectSalitaId, selectTipoDocente);
            }
            if ((rolOriginal.equals("DOCENTE")) && (tipoPerfil.toString().equals("DOCENTE"))) {//si cambio entre tipos de docente
                docenteService.modificarTipoDocente(usuarioOriginal, selectSalitaId, selectTipoDocente);
            }
            if ((rolOriginal.equals("DOCENTE")) && ((tipoPerfil.toString().equals("PADRE")) || (tipoPerfil.toString().equals("DIRECTIVO")))) {// si cambia de docente a no docente
                docenteService.modificarDocenteANoDocente(usuarioOriginal, selectSalitaId, selectTipoDocente);
            }

            usuarioServicio.modificarUsuario(nombre, apellido, password, mail, telefono, nombreUsuario, dni, tipoPerfil);//puede cambiar entre padre y directivo
            return "redirect:/usuario/listarusuarios?msg=modificadoOk";
        }

        return "redirect:/usuario/listarusuarios?msg=error";
    }

    @GetMapping("/modificar")
    public String modificar(@RequestParam Integer dni, ModelMap model) {

        if (dni != null) {
            Usuario usuario = usuarioServicio.buscarUsuario(dni);
            Rol rol = rolService.buscarRol(usuario.getRol().getId());
            model.put("rol", rol);
            model.put("usuario", usuario);
            model.put("accion", "modificar");
// SI el usuario es docente y titular, busco su salita guardada
            if (usuario.getRol().getPerfil().toString().equals("DOCENTE")) {
                model.put("docente", docenteService.buscarDocentePorUsuario(usuario));
                if (docenteService.buscarDocentePorUsuario(usuario).getTipoDocente().toString().equals("TITULAR")) {
                    Salita salitaDocente = salitaService.buscarSalita(docenteService.buscarDocentePorUsuario(usuario).getSalita().getId());
                    model.put("salitaDocente", salitaDocente);
                }
            }
            model.put("salitas", salitaService.buscarSalitas());
        } else {
            model.put("rol", new Rol());
            model.put("usuario", new Usuario());
            model.put("docente", new Docente());
            model.put("accion", "crear");
            model.put("salitas", salitaService.buscarSalitas());
        }

        return "usuario-admin";
    }

    @GetMapping("/formulario")
    public String abrirUsuario(ModelMap modelMap) {

        Usuario usuario = new Usuario();
        Rol rol = new Rol();

        modelMap.put("usuario", usuario);
        modelMap.put("rol", rol);
        modelMap.put("docente", new Docente());
        modelMap.put("accion", "crear");
        modelMap.put("salitas", salitaService.buscarSalitas());
        modelMap.put("tituloPagina", "Registro de Usuarios");

        return "usuario-admin";

    }

    @GetMapping("/baja")
    public String darBaja(@RequestParam Integer dni) {

        try {
            usuarioServicio.darDeBaja(dni);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/usuario/listarusuarios";

    }
}
