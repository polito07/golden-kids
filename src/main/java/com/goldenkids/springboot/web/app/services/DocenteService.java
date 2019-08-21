/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldenkids.springboot.web.app.services;

import com.goldenkids.springboot.web.app.models.Docente;
import com.goldenkids.springboot.web.app.models.TipoDocente;
import com.goldenkids.springboot.web.app.models.Usuario;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author lisandroscofano
 */
@Service
public class DocenteService {

    Logger log = LoggerFactory.getLogger(DocenteService.class);

    @Autowired
    private SalitaService salitaService;

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void crearDocente(Usuario usuario, String salitaId, TipoDocente tipoDocente) {

        Docente docente = new Docente();

        if (usuario != null) {
            docente.setUsuario(usuario);
            log.info("Apellido Usuario: " + docente.getUsuario().getApellido());
        }

        if (tipoDocente != null) {
            docente.setTipoDocente(tipoDocente);
            log.info("Tipo Docente: " + docente.getTipoDocente().toString());
        }

        if (docente.getTipoDocente().toString().equals("TITULAR")) {
            if ((salitaId) != "" || (salitaId != null)) {
                docente.setSalita(salitaService.buscarSalita(salitaId));
                log.info("salita ID: " + salitaId);
                log.info("Nombre Salita: " + docente.getSalita().getNombre());
            }
        }

        em.persist(docente);

    }

    @Transactional
    public void modificarNoDocenteADocente(Usuario usuario, String salitaId, TipoDocente tipoDocente) {
        log.info("Cambie de no docente a docente!");
        Docente docente = new Docente();
        if (tipoDocente.toString().equals("TITULAR")) {
            docente.setSalita(salitaService.buscarSalita(salitaId));
            docente.setTipoDocente(tipoDocente);
            docente.setUsuario(usuario);
        } else if ((tipoDocente.toString().equals("AUXILIAR")) || (tipoDocente.toString().equals("REEMPLAZANTE"))) {
            docente.setTipoDocente(tipoDocente);
            docente.setUsuario(usuario);
        }
        em.persist(docente);
    }

    @Transactional
    public void modificarTipoDocente(Usuario usuario, String salitaId, TipoDocente tipoDocente) {
        Docente docente = buscarDocentePorUsuario(usuario);
        TipoDocente tipoDocenteAnterior = this.buscarDocentePorUsuario(usuario).getTipoDocente();
        log.info("Cambie de tipo de docente! El tipo de docente viejo era : " + tipoDocenteAnterior.toString() + ". El nuevo tipo de docente es : " + tipoDocente.toString());
        if (((tipoDocenteAnterior.toString().equals("AUXILIAR") || tipoDocenteAnterior.toString().equals("REEMPLAZANTE")) && ((tipoDocente.toString().equals("AUXILIAR")) || (tipoDocente.toString().equals("REEMPLAZANTE"))))) {//cambio entre reemplazante y auxiliar, no agrego salita
            docente.setTipoDocente(tipoDocente);
            docente.setUsuario(usuario);//no es necesario porque el usuario no cambia
        } else if (((tipoDocenteAnterior.toString().equals("AUXILIAR") || tipoDocenteAnterior.toString().equals("REEMPLAZANTE")) && (tipoDocente.toString().equals("TITULAR")))) {//si cambia de reemp o aux a titular, agrego salita
            docente.setTipoDocente(tipoDocente);
            docente.setSalita(salitaService.buscarSalita(salitaId));
            docente.setUsuario(usuario);
        } else if ((tipoDocenteAnterior.toString().equals("TITULAR")) && ((tipoDocente.toString().equals("AUXILIAR")) || (tipoDocente.toString().equals("REEMPLAZANTE")))) {//si cambia de titular a aux o reemp
            docente.setTipoDocente(tipoDocente);
            docente.setSalita(null);
            docente.setUsuario(usuario);
        } else if ((tipoDocenteAnterior.toString().equals("TITULAR")) && ((tipoDocente.toString().equals("TITULAR")))) {//si cambia de salita
            docente.setTipoDocente(tipoDocente);
            docente.setSalita(salitaService.buscarSalita(salitaId));
            docente.setUsuario(usuario);
        }
        em.merge(docente);
    }

    @Transactional
    public void modificarDocenteANoDocente(Usuario usuario, String salitaId, TipoDocente tipoDocente) {
        log.info("Cambie de docente a NO docente!");
        Docente docente = buscarDocentePorUsuario(usuario);
        this.eliminarDocente(docente);
    }

    public Docente buscarDocente(String id) {
        return em.find(Docente.class, id);
    }

    public Docente buscarDocentePorUsuario(Usuario usuario) {
        List<Docente> docentePorUsuario = em.createQuery("SELECT d FROM Docente d WHERE d.usuario = :usuario")
                .setParameter("usuario", usuario).getResultList();
        if (docentePorUsuario.size() > 0) {
            return docentePorUsuario.get(0);
        } else {
            return null;
        }
    }

    public void eliminarDocente(Docente docente) {
        em.remove(docente);
    }
}
