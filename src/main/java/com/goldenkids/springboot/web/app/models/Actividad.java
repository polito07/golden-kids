package com.goldenkids.springboot.web.app.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class Actividad {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date inicio;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fin;

    private Integer cantidadLeche;

    @Enumerated(value = EnumType.STRING)
    private TipoActividad tipoActividad;

    @Enumerated(value = EnumType.STRING)
    private TipoCantidad cantidad;

    @Enumerated(value = EnumType.STRING)
    private TipoPanial tipoPanial;

    private String observacion;

    private String usuarioLog;

    @ManyToOne
    private Alumno alumno;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFin() {
        return fin;
    }

    public void setFin(Date fin) {
        this.fin = fin;
    }

    public Integer getCantidadLeche() {
        return cantidadLeche;
    }

    public void setCantidadLeche(Integer cantidadLeche) {
        this.cantidadLeche = cantidadLeche;
    }

    public TipoActividad getTipoActividad() {
        return tipoActividad;
    }

    public void setTipoActividad(TipoActividad tipoActividad) {
        this.tipoActividad = tipoActividad;
    }

    public TipoCantidad getCantidad() {
        return cantidad;
    }

    public void setCantidad(TipoCantidad cantidad) {
        this.cantidad = cantidad;
    }

    public TipoPanial getTipoPanial() {
        return tipoPanial;
    }

    public void setTipoPanial(TipoPanial tipoPanial) {
        this.tipoPanial = tipoPanial;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public String getUsuarioLog() {
        return usuarioLog;
    }

    public void setUsuarioLog(String usuarioLog) {
        this.usuarioLog = usuarioLog;
    }

}
