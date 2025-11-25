package com.example.biblioteca.DTO;

import com.example.biblioteca.Model.Autor;

public class AutorDTO {
    private Integer id;
    private String nombre;
    private String apellido;
    private String telefono;
    private String nacionalidad;

    public AutorDTO(Autor autor) {
        this.id = autor.getId();
        this.nombre = autor.getNombre();
        this.apellido = autor.getApellido();
        this.telefono = autor.getTelefono();
        this.nacionalidad = autor.getNacionalidad();
    }

}
