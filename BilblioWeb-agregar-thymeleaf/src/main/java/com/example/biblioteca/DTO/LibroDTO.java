package com.example.biblioteca.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.example.biblioteca.Model.Libros;



public class LibroDTO {
    private Integer id;
    private String titulo;
    private String genero;
    private String editorial;
    private BigDecimal precio;
    private LocalDate fechaEdicion;
    private AutorDTO autorDTO;

    public LibroDTO(Libros libro) {
        this.id = libro.getId();
        this.titulo = libro.getTitulo();
        this.genero = libro.getGenero();
        this.editorial = libro.getEditorial();
        this.precio = libro.getPrecio();
        this.fechaEdicion = libro.getFechaEdicion();
         
        autorDTO = new AutorDTO(libro.getAutor());
    }

}
