package com.goldenkids.springboot.web.app.services;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.goldenkids.springboot.web.app.models.Alumno;
import com.goldenkids.springboot.web.app.models.Usuario;
import java.util.ArrayList;

@Service
public class PadreService {

    @Autowired
    private EntityManager em;

    public List<Alumno> buscarHijos(Usuario padre) {
        List<Alumno> hijos = new ArrayList<Alumno>();

        hijos = em.createQuery("SELECT a FROM Alumno a WHERE a.contacto = :padre")
                .setParameter("padre", padre).getResultList();

        return hijos;
    }

    public List<Usuario> buscarPadres() {
        List<Usuario> padres = new ArrayList<Usuario>();

        padres = em.createQuery("SELECT u FROM Usuario u WHERE u.rol.perfil = '1'")// 1 corresponde a PADRE
                .getResultList();

        return padres;
    }

}
