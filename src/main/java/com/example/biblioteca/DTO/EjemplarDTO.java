package com.example.biblioteca.DTO;

public class EjemplarDTO {
    private String titulo;
    private String categoria;
    private String nombreAutor;
    private String apellidoAutor;
    
    // constructor, getters y setters
    public EjemplarDTO(String titulo, String categoria, String nombreAutor, String apellidoAutor) {
        this.titulo = titulo;
        this.categoria = categoria;
        this.nombreAutor = nombreAutor;
        this.apellidoAutor = apellidoAutor;
    }
}
