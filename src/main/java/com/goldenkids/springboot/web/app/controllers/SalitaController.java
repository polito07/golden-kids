package com.goldenkids.springboot.web.app.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

import com.goldenkids.springboot.web.app.models.Salita;
import com.goldenkids.springboot.web.app.services.SalitaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/salita")
public class SalitaController {

    @Autowired
    private SalitaService salitaService;

    Logger log = LoggerFactory.getLogger(ActividadesController.class);

    @RequestMapping("/listarsalitas")
    public String listar(@RequestParam(required = false) String q, Model model, String msg) {

        List<Salita> salitas;
        if (q != null) {
            salitas = salitaService.buscarSalitas(q);
        } else {
            salitas = salitaService.buscarSalitas();
        }

        if (msg != null) {
            switch (msg) {
                case "guardadoOk":
                    model.addAttribute("success", "La Salita ha sido creada con éxito.");
                    break;
                case "modificadoOk":
                    model.addAttribute("success", "La Salita ha sido modificada con éxito.");
                    break;
                case "error":
                    model.addAttribute("error", "Ha ocurrido un error con la salita.");
                    break;
                default:
                    break;
            }
        }

        model.addAttribute("salitas", salitas);
        model.addAttribute("q", q);
        model.addAttribute("pagina", "Salita");

        model.addAttribute("tituloPagina", "Administración de Salitas");
        model.addAttribute("subtituloPagina", "Utilice este modulo para administrar los registros de Salitas del jardin.");

        return "salita-listado";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("salita") @RequestParam String nombre, String horaEntrada, String horaSalida,
            String accion, String salitaId) throws ParseException {

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

        Date horaEntradaFormateada = null;
        Date horaSalidaFormateada = null;
        log.info("La hora de entrada es: " + horaEntrada);
        if (horaEntrada != "") {
            horaEntradaFormateada = formatter.parse(horaEntrada);
        }
        if (horaSalida != "") {
            horaSalidaFormateada = formatter.parse(horaSalida);
        }

        if (accion.equals("crear")) {
            salitaService.crearSalita(nombre, horaEntradaFormateada, horaSalidaFormateada);
            return "redirect:/salita/listarsalitas?msg=guardadoOk";
        } else if (accion.equals("modificar")) {
            salitaService.modificarSalita(nombre, horaEntradaFormateada, horaSalidaFormateada, salitaId);
            return "redirect:/salita/listarsalitas?msg=modificadoOk";
        }
        return "redirect:/salita/listarsalitas?msg=error";
    }

    @GetMapping("/modificar")
    public String modificar(@RequestParam String id, ModelMap model) {

        if (id != null) {
            Salita salita = salitaService.buscarSalita(id);
            model.put("salita", salita);
            model.put("accion", "modificar");
            model.put("salitas", salitaService);
            model.put("tituloPagina", "Registro de Salitas");
        } else {
            model.put("salita", new Salita());
            model.put("accion", "modificar");
            model.put("salitas", salitaService);
            model.put("tituloPagina", "Registro de Salitas");
        }

        return "salita-admin";
    }

    @GetMapping("/formulario")
    public String abrirSalita(ModelMap modelMap) {

        Salita salita = new Salita();

        modelMap.put("salita", salita);
        modelMap.put("accion", "crear");
        modelMap.put("salitas", salitaService.buscarSalitas());
        modelMap.put("tituloPagina", "Registro de Salitas");

        return "salita-admin";
    }

}
