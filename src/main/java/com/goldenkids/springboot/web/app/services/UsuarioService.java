package com.goldenkids.springboot.web.app.services;

import com.goldenkids.springboot.web.app.models.Rol;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.goldenkids.springboot.web.app.models.TipoPerfil;
import com.goldenkids.springboot.web.app.models.Usuario;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UsuarioService {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void crearUsuario(String nombre, String apellido, String password, String mail, String telefono,
            String nombreUsuario, int dni, TipoPerfil tipoPerfil) {

        Usuario usuario = new Usuario();
        Rol rol = new Rol();
        rol.setPerfil(tipoPerfil);

        usuario.setApellido(apellido);
        usuario.setNombre(nombre);
        usuario.setDni(dni);
        usuario.setMail(mail);
        usuario.setNombreUsuario(nombreUsuario);

        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setTelefono(telefono);
        usuario.setRol(rol);

        usuario.setFechaAlta(new Date());
        usuario.setFechaBaja(null);

        if (buscarUsuario(dni) == null) {
            em.persist(usuario);
        }

    }

    @Transactional
    public void modificarUsuario(String nombre, String apellido, String password, String mail, String telefono,
            String nombreUsuario, int dni, TipoPerfil tipoPerfil) {

        Usuario usuario = new Usuario();
        Rol rol = new Rol();
        rol.setPerfil(tipoPerfil);

        usuario.setApellido(apellido);
        usuario.setNombre(nombre);
        usuario.setDni(dni);
        usuario.setMail(mail);
        usuario.setNombreUsuario(nombreUsuario);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setTelefono(telefono);
        usuario.setRol(rol);

        usuario.setFechaAlta(new Date());
        usuario.setFechaBaja(null);

        em.merge(usuario);

    }

    @Transactional
    public void eliminar(Integer dni) throws Exception {
        Usuario usuario = buscarUsuario(dni);
        em.remove(usuario);
    }

    @Transactional
    public void darDeBaja(Integer dni) throws Exception {

        Usuario usuarioBaja = em.find(Usuario.class, dni);

        usuarioBaja.setFechaBaja(new Date());

    }

    public Usuario buscarUsuario(Integer dni) {
        return em.find(Usuario.class, dni);
    }

    @SuppressWarnings("unchecked")
    public List<Usuario> buscarUsuarios(String q) {
        return em.createQuery("SELECT c FROM Usuario c WHERE c.nombre LIKE :q OR c.dni LIKE :q OR c.apellido LIKE :q")
                .setParameter("q", "%" + q + "%").getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Usuario> buscarUsuarios() {
        return em.createQuery("SELECT c FROM Usuario c WHERE c.fechaBaja is null").getResultList();
    }

    public Usuario buscarUsuarioPorUserName(String user) {
        Usuario usuario = (Usuario) em.createQuery("SELECT c FROM Usuario c WHERE c.nombreUsuario = :user")
                .setParameter("user", user).getResultList().stream().findFirst().orElse(null);
        return usuario;
    }

    @SuppressWarnings("unchecked")
    public List<Usuario> buscarUsuariosEliminados() {
        return em.createQuery("SELECT c FROM Usuario c WHERE c.fechaBaja is not null").getResultList();
    }

}
