package com.example.biblioteca.Controlador;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.biblioteca.Model.RecursoBibliografico;
import com.example.biblioteca.Servicicos.RecursoServicio;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/ejemplares")

public class RecursoControlador {

    @Autowired
    private RecursoServicio rbServicio;

    @GetMapping
    public ResponseEntity<List<RecursoBibliografico>> listarRecursosBibliograficos(@RequestParam String param) {
        List<RecursoBibliografico> recursosBibliograficos = rbServicio.listarRecursosBibliograficos();
        return new ResponseEntity<>(recursosBibliograficos, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RecursoBibliografico> buscarRecursoBibliograficoCodigoBarras(@PathVariable String codigoBarras) {
        Optional<RecursoBibliografico> ejemplar = rbServicio.obtenerRecursoBibliograficoCodigoDeBarras(codigoBarras);
        boolean existe = ejemplar.isPresent();

        if (existe) {
            return new ResponseEntity<>(ejemplar.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);   
        }
    }

    @GetMapping("/buscar/{palabraClave}")
    public ResponseEntity<List<RecursoBibliografico>> buscarEjemplarePorPalabraClave(@PathVariable String palabraClave) {
        Optional<List<RecursoBibliografico>> recursosBibliograficos = Optional.ofNullable(rbServicio.buscarPorPalabraClave(palabraClave));

        if (recursosBibliograficos != null) {
            return new ResponseEntity<>(recursosBibliograficos.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
    }

    @GetMapping("/buscar/titulo-autor/{titulo}/{nombreAutor}")
    public ResponseEntity<Set<RecursoBibliografico>> buscarEjemplaresPorTituloAutor(@PathVariable String titulo, @PathVariable String nombreAutor) {
        Set<RecursoBibliografico> recursosBibliograficos = rbServicio.buscarPorTituloAutor(titulo, nombreAutor);
        boolean existe = recursosBibliograficos != null && !recursosBibliograficos.isEmpty();

        if (existe) {
            return new ResponseEntity<>(recursosBibliograficos, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/buscar/tema/{tema}")
    public ResponseEntity<Set<RecursoBibliografico>> buscarEjemplaresPorTema(@PathVariable String tema) {
        Set<RecursoBibliografico> ejemplares = rbServicio.buscarPorTema(tema);
        boolean existe = ejemplares != null && !ejemplares.isEmpty();

        if (existe) {
            return new ResponseEntity<>(ejemplares, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    

    @PostMapping
    public ResponseEntity<RecursoBibliografico> crearEjemplar(@RequestBody RecursoBibliografico recursoBibliografico) {
        RecursoBibliografico nuevoRecurso = rbServicio.guardarRecursoBibliografico(recursoBibliografico);
        return new ResponseEntity<>(nuevoRecurso, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecursoBibliografico> actualizarRecursoBibliografico(@PathVariable String codigoBarras, @RequestBody RecursoBibliografico recursoBiblio) {
        Optional<RecursoBibliografico> rbExistente = rbServicio.obtnerRecursoBibliograficoPorIsbn(codigoBarras);
        boolean existe = rbExistente.isPresent();

        if (existe) {
            RecursoBibliografico recursoActualizado = rbExistente.get();
            String titulo = recursoBiblio.getTitulo();
            String estado = recursoBiblio.getEstado();
            String categoria = recursoBiblio.getCategoria();
            String estadoFisico = recursoBiblio.getEstadoFisico();

            if (titulo != null) {
                recursoActualizado.setTitulo(titulo);
            }
            if (estado != null) {
                recursoActualizado.setEstado(estado);  //Esto lo debe corroborar javascript
            }
            if (categoria != null) {
                recursoActualizado.setCategoria(categoria);
            }
            if (estadoFisico != null) {
                recursoActualizado.setEstadoFisico(estadoFisico);
            }

            RecursoBibliografico recursoGuardado = rbServicio.guardarRecursoBibliografico(recursoActualizado);
            return new ResponseEntity<>(recursoGuardado, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{codigoBarras}")
    public ResponseEntity<Void> borrarRecursoBibliografico(@PathVariable String codigoBarras) {
        try {
            rbServicio.eliminarRecursoBibliografico(codigoBarras);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
           return ResponseEntity.notFound().build();
        }
    }


    
    

}
