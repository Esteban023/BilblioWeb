package com.example.biblioteca.DTO;

import com.example.biblioteca.Model.Reserva;

public class ReservaDTO {
    boolean exito;
    String mensaje;
    private Reserva reserva;
    private Integer idUsuario;
    private String codigoDeBarras;

    public ReservaDTO() {
    }

    public ReservaDTO(String mensaje, boolean exito) {
        this.exito = exito;
        this.mensaje = mensaje;
    }
    
    public ReservaDTO(String mensaje, boolean exito, Reserva reserva) {  //Response
        this.exito = exito;
        this.reserva = reserva;
        this.mensaje = mensaje;
    }

    public ReservaDTO(Integer idUsuario, String codigoDeBarras) { //Request
        this.idUsuario = idUsuario;
        this.codigoDeBarras = codigoDeBarras;
    }

    public boolean isExito() {
        return exito;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCodigoDeBarras() {
        return codigoDeBarras;
    }

    public void setCodigoDeBarras(String codigoDeBarras) {
        this.codigoDeBarras = codigoDeBarras;
    }

}
