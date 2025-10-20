package com.example.biblioteca.Model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity

public class Autor {
    @jakarta.persistence.Id
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)

    private Integer id;
    private String nombre;
    private String apellido;
    private String telefono;
    private String nacionalidad;

    @OneToMany
    List<Libros> libros;


    @ManyToMany(mappedBy = "autores")
    Set<RecursoBibliografico> recursosBibliograficos = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public List<Libros> getLibros() {
        return libros;
    }

    public void setLibros(List<Libros> libros) {
        this.libros = libros;
    }

    public Set<RecursoBibliografico> getRecursoBibliograficos() {
        return recursosBibliograficos;
    }

    public void setRecursoBibliograficos(Set<RecursoBibliografico> recursosBibliograficos) {
        this.recursosBibliograficos = recursosBibliograficos;
    }
  
}
