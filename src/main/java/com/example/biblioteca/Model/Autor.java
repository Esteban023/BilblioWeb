package com.example.biblioteca.Model;

import java.util.Set;
import java.util.HashSet;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Autor {
    @jakarta.persistence.Id
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)

    private Integer id;
    private String nombre;
    private String apellido;
    private String telefono;
    private String nacionalidad;

    @ManyToMany(mappedBy = "autores")
    @JsonIgnore
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

    public Set<RecursoBibliografico> getRecursoBibliograficos() {
        return recursosBibliograficos;
    }

    public void setRecursoBibliograficos(Set<RecursoBibliografico> recursosBibliograficos) {
        this.recursosBibliograficos = recursosBibliograficos;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // mismo objeto
        if (obj == null || getClass() != obj.getClass()) return false; // distinto tipo
        Autor autor = (Autor) obj;
        return id != null && id.equals(autor.getId());
    }
  
    
}
