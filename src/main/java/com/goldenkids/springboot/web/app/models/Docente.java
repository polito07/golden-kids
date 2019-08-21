package com.goldenkids.springboot.web.app.models;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Docente {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private Salita salita;

    @Enumerated(value = EnumType.STRING)
    private TipoDocente tipoDocente;

    public Docente() {
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Salita getSalita() {
        return salita;
    }

    public void setSalita(Salita salita) {
        this.salita = salita;
    }

    public TipoDocente getTipoDocente() {
        return tipoDocente;
    }

    public void setTipoDocente(TipoDocente tipoDocente) {
        this.tipoDocente = tipoDocente;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
