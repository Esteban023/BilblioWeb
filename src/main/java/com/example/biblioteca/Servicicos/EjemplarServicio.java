package com.example.biblioteca.Servicicos;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import com.example.biblioteca.Model.Ejemplar;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.biblioteca.bibliotecaRepositorio.EjemplarRepositorio;

@Service
public class EjemplarServicio {

    @Autowired
    private EjemplarRepositorio ejemplarRepositorio;

    public List<Ejemplar> listarEjemplares() {
        return ejemplarRepositorio.findAll();
    }


    public Optional<Ejemplar> obtenerEjemplarPorId(Integer id) {
        return ejemplarRepositorio.findById(id);
    }

    public Ejemplar guardarEjemplar(Ejemplar ejemplar) {
        return ejemplarRepositorio.save(ejemplar);
    }

    public void eliminarEjemplar(Integer id) {
        ejemplarRepositorio.deleteById(id);
    } 

    public List<Ejemplar> buscarPorPalabraClave(String palabraClave) {
        return ejemplarRepositorio.buscarPorPalabraClave(palabraClave);
    }

}
