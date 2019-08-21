package com.goldenkids.springboot.web.app.services;

import com.goldenkids.springboot.web.app.models.Alumno;
import com.goldenkids.springboot.web.app.models.Inscripcion;
import com.goldenkids.springboot.web.app.models.Salita;
import java.util.Date;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Service
public class InscripcionService {

    Logger log = LoggerFactory.getLogger(SalitaService.class);

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private SalitaService salitaService;

    @Autowired
    private AlumnoService alumnoService;

    @Transactional
    public void crearInscripcion(Integer selectAlumnoDni, String selectSalitaId, Date fechaAlta, Date fechaBaja) {

        Inscripcion inscripcion = new Inscripcion();
        Alumno alumno = alumnoService.buscarAlumno(selectAlumnoDni);
        Salita salita = salitaService.buscarSalita(selectSalitaId);

        inscripcion.setAlumno(alumno);
        inscripcion.setSalita(salita);
        inscripcion.setFechaAlta(fechaAlta);
        inscripcion.setFechaBaja(fechaBaja);

        em.persist(inscripcion);

    }

    @Transactional
    public void modificarInscripcion(Integer selectAlumnoDni, String selectSalitaId, Date fechaAlta, Date fechaBaja, String inscripcionId) {

        Inscripcion inscripcion = buscarInscripcion(inscripcionId);
        Alumno alumno = alumnoService.buscarAlumno(selectAlumnoDni);
        Salita salita = salitaService.buscarSalita(selectSalitaId);

        inscripcion.setAlumno(alumno);
        inscripcion.setSalita(salita);
        inscripcion.setFechaAlta(fechaAlta);
        inscripcion.setFechaBaja(fechaBaja);

        em.merge(inscripcion);

    }

    public boolean yaInscripto(Integer alumnoDni) {
        Alumno alumno = alumnoService.buscarAlumno(alumnoDni);

        List<Inscripcion> inscripcionesDelAlumno = buscarInscripcionesPorAlumno(alumno);
        Boolean inscripcionAbierta = false;

        for (Inscripcion inscripcion : inscripcionesDelAlumno) {
            if (inscripcion.getFechaBaja() == null) {
                inscripcionAbierta = true;
            }
        }

        return inscripcionAbierta;
    }

    public Inscripcion buscarInscripcion(String id) {
        return em.find(Inscripcion.class, id);
    }

    @SuppressWarnings("unchecked")
    public List<Inscripcion> buscarInscripciones(String q) {
        return em.createQuery("SELECT i FROM Inscripcion i WHERE i.alumno.nombre LIKE :q OR i.alumno.dni LIKE :q OR i.alumno.apellido LIKE :q")
                .setParameter("q", "%" + q + "%").getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Inscripcion> buscarInscripciones() {
        return em.createQuery("SELECT i FROM Inscripcion i WHERE i.fechaBaja is null").getResultList();
    }

    public List<Inscripcion> buscarInscripcionesPorAlumno(Alumno alumno) {
        return em.createQuery("SELECT i FROM Inscripcion i WHERE i.alumno = :alumno")
                .setParameter("alumno", alumno).getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Inscripcion> buscarInscripcionesEliminadas() {
        return em.createQuery("SELECT i FROM Inscripcion i WHERE i.fechaBaja is not null").getResultList();
    }

    public Inscripcion buscarInscripcionActual(Alumno alumno) {
        Inscripcion inscripcion;
        try {
            inscripcion = (Inscripcion) em.createQuery("SELECT i FROM Inscripcion i WHERE (i.alumno = :alumno) AND (i.fechaBaja = null)")
                    .setParameter("alumno", alumno)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException nre) {
            log.error("No hay resultados para el query de Inscripcion" + nre.getMessage());
            inscripcion = null;
        } catch (NonUniqueResultException nure) {
            log.error("Hay mas de un resultado para el query de Inscripcion" + nure.getMessage());
            inscripcion = null;
        }
        return inscripcion;
    }

}
