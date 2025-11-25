package com.example.biblioteca.Model;

import java.util.UUID;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;

@Entity
public class Reserva {
    @Id
    @Column(name = "codigo_reserva", nullable = false, unique = true)
    private String codigoReserva;
    
    // Relación con el usuario que realiza la reserva
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    
    // Relación con el recurso bibliográfico reservado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codigo_de_barras")
    private RecursoBibliografico recursoBibliografico;
    
    // Fecha y hora en que se realizó la reserva
    @Column(nullable = false)
    private LocalDateTime fechaReserva;
    
    // Fecha límite para retirar el material
    @Column(nullable = false)
    private LocalDateTime fechaLimiteRetiro;
    
    // Estado de la reserva
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReserva estado;
    
    // Posición en la cola de reservas (si hay múltiples reservas para el mismo recurso)
    @Column(name = "posicion_cola", nullable = true)
    private Integer posicionCola;
    
    // Método para generar código único de reserva
    private String generarCodigoReserva() {
        return "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public enum EstadoReserva {
        ACTIVA,           // Reserva activa y vigente
        CONCRETADA,       // Se convirtió en préstamo
        CANCELADA       // Cancelada por el usuario
    }

    public String getCodigoReserva() {
        return codigoReserva;
    }

    public void setCodigoReserva() {
        this.codigoReserva = generarCodigoReserva();
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public RecursoBibliografico getRecursoBibliografico() {
        return recursoBibliografico;
    }

    public void setRecursoBibliografico(RecursoBibliografico recursoBibliografico) {
        this.recursoBibliografico = recursoBibliografico;
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDateTime fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public LocalDateTime getFechaLimiteRetiro() {
        return fechaLimiteRetiro;
    }

    public void setFechaLimiteRetiro(LocalDateTime fechaLimiteRetiro) {
        this.fechaLimiteRetiro = fechaLimiteRetiro;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public Integer getPosicionCola() {
        return posicionCola;
    }

    public void setPosicionCola(Integer posicionCola) {
        this.posicionCola = posicionCola;
    }

}
