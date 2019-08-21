package com.goldenkids.springboot.web.app.services;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.goldenkids.springboot.web.app.models.Alumno;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AlumnoService {

    Logger log = LoggerFactory.getLogger(AlumnoService.class);

    @Autowired
    private UsuarioService usuarioService;

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void crearAlumno(Integer dni, String nombre, String apellido, Date fechaNacimiento, Integer padreDni, String uniqueFileName) {

        Alumno alumno = new Alumno();

        alumno.setDni(dni);
        alumno.setNombre(nombre);
        alumno.setApellido(apellido);
        alumno.setFechaNacimiento(fechaNacimiento);
        if (padreDni != null) {
            alumno.setContacto(usuarioService.buscarUsuario(padreDni));
        }
        alumno.setFoto(uniqueFileName);

        if (buscarAlumno(dni) == null) {
            em.persist(alumno);
        } else {

        }

    }

    @Transactional
    public void modificarAlumno(Integer dni, String nombre, String apellido, Date fechaNacimiento, Integer padreDni, String uniqueFileName) {

        Alumno alumno = buscarAlumno(dni);

        alumno.setNombre(nombre);
        alumno.setApellido(apellido);
        alumno.setFechaNacimiento(fechaNacimiento);
        alumno.setContacto(usuarioService.buscarUsuario(padreDni));

        log.info("nombre de archivo " + uniqueFileName);

        if (uniqueFileName != null) {
            alumno.setFoto(uniqueFileName);//si cambio el nombre de la foto persisto, sino queda la anterior
        }

        em.merge(alumno);

    }

    @Transactional
    public void darDeBaja(Integer dni) throws Exception {

        Alumno alumnoBaja = em.find(Alumno.class, dni);

        alumnoBaja.setFechaBaja(new Date());

    }

    public Alumno buscarAlumno(Integer dni) {
        return em.find(Alumno.class, dni);
    }

    @SuppressWarnings("unchecked")
    public List<Alumno> buscarAlumnos(String q) {
        return em.createQuery("SELECT c FROM Alumno c WHERE c.nombre LIKE :q OR c.apellido LIKE :q OR c.dni LIKE :q")
                .setParameter("q", "%" + q + "%").getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Alumno> buscarAlumnos() {
        return em.createQuery("SELECT a FROM Alumno a WHERE a.fechaBaja is null").getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Alumno> buscarAlumnosEliminados() {
        return em.createQuery("SELECT a FROM Alumno a WHERE a.fechaBaja is not null").getResultList();
    }

}
