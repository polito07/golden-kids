package com.goldenkids.springboot.web.app.services;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.goldenkids.springboot.web.app.models.Salita;


@Service
public class SalitaService {

    Logger log = LoggerFactory.getLogger(SalitaService.class);

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void crearSalita(String nombre, Date horaEntradaFormateada, Date horaSalidaFormateada) {
        Salita salita = new Salita();

        salita.setHoraEntrada(horaEntradaFormateada);

        salita.setHoraSalida(horaSalidaFormateada);

        salita.setNombre(nombre);
        em.persist(salita);
    }

    @Transactional
    public void modificarSalita(String nombre, Date horaEntradaFormateada, Date horaSalidaFormateada, String salitaId) {
        Salita salita = buscarSalita(salitaId);

        salita.setHoraEntrada(horaEntradaFormateada);
        salita.setHoraSalida(horaSalidaFormateada);
        salita.setNombre(nombre);

        em.merge(salita);

    }

    public Salita buscarSalita(String id) {
        Salita salita = em.find(Salita.class, id);
        if (salita != null) {
            return salita;
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Salita> buscarSalitas(String q) {
        return em.createQuery("SELECT c FROM Salita c WHERE c.nombre LIKE :q OR c.id LIKE :q")
                .setParameter("q", "%" + q + "%").getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Salita> buscarSalitas() {
        List<Salita> salitas = new ArrayList<>();
        salitas = em.createQuery("SELECT s FROM Salita s").getResultList();
        return salitas;
    }

    public void eliminarSalita(Salita salita) {
        em.remove(salita);
    }

}
