package com.example.biblioteca.Model;

import java.util.Date;
import java.time.LocalDate;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity

public class Prestamo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
    private int id;
    private boolean estado;
    private Date fechaDevolucion;
    private LocalDate fechaAdquisicion;

    @OneToOne
    private RecursoBibliografico recursoBibliografico; 

    @ManyToOne
    private Usuario usuario; // Relación con Usuario

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public boolean getEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public Date getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(Date fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public LocalDate getFechaAdquisicion() {
        return fechaAdquisicion;
    }

    public void setFechaAdquisicion(LocalDate fechaAdquisicion) {
        this.fechaAdquisicion = fechaAdquisicion;
    }

    public RecursoBibliografico getEjemplar() {
        return recursoBibliografico;
    }

    public void setEjemplar(RecursoBibliografico recursoBibliografico) {
        this.recursoBibliografico = recursoBibliografico;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }



}
