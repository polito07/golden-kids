/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldenkids.springboot.web.app.services;

import com.goldenkids.springboot.web.app.models.Rol;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;

/**
 *
 * @author lisandroscofano
 */
@Service
public class RolService {

    @PersistenceContext
    private EntityManager em;

    public Rol buscarRol(String id) {
        Rol rol = em.find(Rol.class, id);
        if (rol != null) {
            return rol;
        } else {
            return null;
        }
    }

}
