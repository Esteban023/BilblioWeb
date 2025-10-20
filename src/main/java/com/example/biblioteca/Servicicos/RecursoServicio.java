package com.example.biblioteca.Servicicos;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.example.biblioteca.Model.RecursoBibliografico;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.biblioteca.bibliotecaRepositorio.RecursoRepositorio;

@Service
public class RecursoServicio {

    @Autowired
    private RecursoRepositorio rbRepositorio;

    public List<RecursoBibliografico> listarRecursosBibliograficos() {
        return rbRepositorio.findAll();
    }

    public Optional<RecursoBibliografico> obtenerRecursoBibliograficoCodigoDeBarras(String codigoBarras) {
        return rbRepositorio.findById(codigoBarras);
    }

    public Optional<RecursoBibliografico> obtnerRecursoBibliograficoPorIsbn(String isbn) {
        return rbRepositorio.buscarPorIsbn(isbn);
    }

    public RecursoBibliografico guardarRecursoBibliografico(RecursoBibliografico recursoBiblio) {
        return rbRepositorio.save(recursoBiblio);
    }

    public RecursoBibliografico actualizarRecursoBibliografico(String codigoBarras, RecursoBibliografico recursoBiblio) {

        RecursoBibliografico rbExistente = rbRepositorio.findById(codigoBarras).orElseThrow(
            () -> RuntimeException("Ejemplar no encontrado con id: ", codigoBarras)
        );

        return rbRepositorio.save(rbExistente);
    }

    public void eliminarRecursoBibliografico(String codigoDeBarras) {
        rbRepositorio.deleteById(codigoDeBarras);
    } 

    public void eliminarRecursoBibliograficoPorIsbn(String isbn) {
        rbRepositorio.deleteByIsbn(isbn);
    }

    public List<RecursoBibliografico> buscarPorPalabraClave(String palabraClave) {
        return rbRepositorio.buscarPorPalabraClave(palabraClave);
    }

    public Set<RecursoBibliografico> buscarPorTituloAutor(String titulo, String nombreAutor) {
        return rbRepositorio.buscarRecursoBibliograficoPorTituloNombreAutor(titulo, nombreAutor);
    }

    public Set<RecursoBibliografico> buscarPorTema(String tema) {
        return rbRepositorio.encontrarPorTema(tema);
    }

    private RuntimeException RuntimeException(String string, Object primaryKey) {
        throw new RuntimeException(string + primaryKey);
    }

}
