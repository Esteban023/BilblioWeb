package com.example.biblioteca.Servicicos;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.biblioteca.LibrosRepositorio.LibrosRepositorio;
import com.example.biblioteca.Model.Libros;

@Service

public class LibrosServicios {
    @Autowired  
    private LibrosRepositorio librosRepositorio;

    public List<Libros> listarLibros() {
        return librosRepositorio.findAll();
    }

    public Optional<Libros> obtenerLibroPorId(Integer id) {
        return librosRepositorio.findById(id);
    }

    public Libros guardarLibro(Libros libro) {
        return (Libros) librosRepositorio.save(libro);
    }

    public void eliminarLibro(Integer id) {
        librosRepositorio.deleteById(id);
    }

}
