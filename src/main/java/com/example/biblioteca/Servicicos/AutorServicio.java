package com.example.biblioteca.Servicicos;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.biblioteca.LibrosRepositorio.AutorRepositorio;
import com.example.biblioteca.Model.Autor;

@Service
public class AutorServicio {
    @Autowired
    private AutorRepositorio autorRepositorio;
    
    public List<Autor> listarAutores() {
        return autorRepositorio.findAll();
    }

    public Optional<Autor> obtenerAutorPorId(Integer id) {
        return autorRepositorio.findById(id);
    }

    public Autor guardarAutor(Autor autor) {
        return (Autor) autorRepositorio.save(autor);
    }

    public void eliminarAutor(Integer id) {
        autorRepositorio.deleteById(id);
    }

    public Autor actualizarAutor(int id, Autor autor) {
        Autor autorExistente = autorRepositorio.findById(id).orElseThrow(() -> {
            throw new RuntimeException("Autor no encontrado con id: " + id);
        });

        return autorRepositorio.save(autorExistente);
    
    }


}
