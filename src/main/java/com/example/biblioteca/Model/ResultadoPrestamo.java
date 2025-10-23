package com.example.biblioteca.Model;

public class ResultadoPrestamo {
    private boolean exito;
    private String mensaje;
    private Prestamo prestamo;

    public ResultadoPrestamo(boolean exito, String mensaje) {
        this.exito = exito;
        this.mensaje = mensaje;
    }

    public ResultadoPrestamo(boolean exito, String mensaje, Prestamo prestamo) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.prestamo = prestamo;
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
    
    public Prestamo getPrestamo() {
        return prestamo;
    }

    public void setPrestamo(Prestamo prestamo) {
        this.prestamo = prestamo;
    }

    
}
