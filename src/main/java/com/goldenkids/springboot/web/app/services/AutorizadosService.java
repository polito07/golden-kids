package com.goldenkids.springboot.web.app.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.goldenkids.springboot.web.app.models.Autorizados;

import java.util.Date;

@Service
public class AutorizadosService {

    @Autowired
    private AlumnoService alumnoService;

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void crearAutorizados(String nombre, String apellido, String telefono1, String telefono2, int dni,
            String parentesco, String error, Integer alumnoDni) throws Exception {

        Autorizados autorizados = new Autorizados();

        autorizados.setApellido(apellido);
        autorizados.setNombre(nombre);
        autorizados.setDni(dni);
        autorizados.setTelefono1(telefono1);
        autorizados.setTelefono2(telefono2);
        autorizados.setParentesco(parentesco);
        if (alumnoDni != null) {
            autorizados.setAlumno(alumnoService.buscarAlumno(alumnoDni));
        } else {
            autorizados.setAlumno(null);
        }
        em.persist(autorizados);
    }

    @Transactional
    public void modificarAutorizados(String nombre, String apellido, String telefono1, String telefono2, Integer dni,
            String parentesco, String error, Integer alumnoDni, String id) {

        Autorizados autorizados = buscarAutorizadosPorId(id);

        autorizados.setApellido(apellido);
        autorizados.setNombre(nombre);
        autorizados.setDni(dni);
        autorizados.setTelefono1(telefono1);
        autorizados.setTelefono2(telefono2);
        autorizados.setParentesco(parentesco);
        if (alumnoDni != null) {
            autorizados.setAlumno(alumnoService.buscarAlumno(alumnoDni));
        } else {
            autorizados.setAlumno(null);
        }
        autorizados.setFechaBaja(null);

        em.merge(autorizados);

    }

    public Autorizados buscarAutorizadosPorId(String id) {
        return em.find(Autorizados.class, id);
    }

    @SuppressWarnings("unchecked")
    public List<Autorizados> buscarAutorizados(String q) {
        return em.createQuery("SELECT c FROM Autorizados c WHERE c.nombre LIKE :q OR c.dni LIKE :q OR c.alumno.nombre LIKE :q OR c.alumno.apellido LIKE :q")
                .setParameter("q", "%" + q + "%").getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Autorizados> buscarAutorizados() {
        return em.createQuery("SELECT a FROM Autorizados a WHERE a.fechaBaja is null").getResultList();
    }

    @Transactional
    public void eliminar(String id) throws Exception {
        Autorizados autorizados = buscarAutorizadosPorId(id);
        em.remove(autorizados);
    }

    @Transactional
    public void darDeBaja(String id) throws Exception {

        Autorizados autorizadosBaja = em.find(Autorizados.class, id);

        autorizadosBaja.setFechaBaja(new Date());

    }

    @SuppressWarnings("unchecked")
    public List<Autorizados> buscarAutorizadosEliminados() {
        return em.createQuery("SELECT a FROM Autorizados a WHERE a.fechaBaja is not null").getResultList();
    }

}
