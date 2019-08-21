package com.goldenkids.springboot.web.app.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
public class Alumno {

    @Id
    private int dni;

    private String nombre;
    private String apellido;

    @Temporal(TemporalType.DATE)
    private Date fechaNacimiento;
    @Temporal(TemporalType.DATE)
    private Date fechaBaja;
    @OneToOne
    private Usuario contacto;
//    @ManyToOne
//    private Salita salita;

    private String foto;

    @Transient
    private Salita salita;

    @Transient
    private boolean enClase = false;

    @Transient
    private boolean durmiendo = false;

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Salita getSalita() {
        return salita;
    }

    public void setSalita(Salita salita) {
        this.salita = salita;
    }

    public Date getFechaBaja() {
        return fechaBaja;
    }

    public void setFechaBaja(Date fechaBaja) {
        this.fechaBaja = fechaBaja;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Usuario getContacto() {
        return contacto;
    }

    public void setContacto(Usuario contacto) {
        this.contacto = contacto;
    }

    public boolean isEnClase() {
        return enClase;
    }

    public void setEnClase(boolean enClase) {
        this.enClase = enClase;
    }

    public boolean isDurmiendo() {
        return durmiendo;
    }

    public void setDurmiendo(boolean durmiendo) {
        this.durmiendo = durmiendo;
    }

    @Override
    public String toString() {
        return "Alumno{" + "dni=" + dni + ", nombre=" + nombre + ", apellido=" + apellido + ", fechaNacimiento=" + fechaNacimiento + ", fechaBaja=" + fechaBaja + ", contacto=" + contacto + ", salita=" + salita + ", foto=" + foto + ", enClase=" + enClase + ", durmiendo=" + durmiendo + '}';
    }

}
