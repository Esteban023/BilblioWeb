package com.example.biblioteca.Controlador;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.biblioteca.bibliotecaRepositorio.EjemplarRepositorio;
import com.example.biblioteca.Model.Ejemplar;
import com.example.biblioteca.Servicicos.EjemplarServicio;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/ejemplares")

public class EjemplarControlador {
    @Autowired
    private EjemplarRepositorio ejemplarRepositorio;

    @Autowired
    private EjemplarServicio ejemplarServicio;

    @GetMapping
    public ResponseEntity<List<Ejemplar>> listarEjemplares(@RequestParam String param) {
        List<Ejemplar> ejemplares = ejemplarRepositorio.findAll();
        return new ResponseEntity<>(ejemplares, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Ejemplar> obtenerEjemplarPorId(@PathVariable int id) {
        Optional<Ejemplar> ejemplar = ejemplarRepositorio.findById(id);
        boolean existe = ejemplar.isPresent();

        if (existe) {
            return new ResponseEntity<>(ejemplar.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);   
        }
    }

    @GetMapping("/buscar/{palabraClave}")
    public ResponseEntity<List<Ejemplar>> buscarEjemplarePorPalabraClave(@PathVariable String palabraClave) {
        Optional<List<Ejemplar>> ejemplares = Optional.ofNullable(ejemplarRepositorio.buscarPorPalabraClave(palabraClave));

        if (ejemplares != null) {
            return new ResponseEntity<>(ejemplares.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
    }

    @GetMapping("/buscar/titulo-autor/{titulo}/{nombreAutor}")
    public ResponseEntity<List<Ejemplar>> buscarEjemplaresPorTituloAutor(@PathVariable String titulo, @PathVariable String nombreAutor) {
        List<Ejemplar> ejemplares = ejemplarRepositorio.buscarLibroPorTituloNombreAutor(titulo, nombreAutor);
        boolean existe = ejemplares != null && !ejemplares.isEmpty();

        if (existe) {
            return new ResponseEntity<>(ejemplares, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/bucsar/genero/{genero}")
    public ResponseEntity<List<Ejemplar>> buscarEjemplaresPorGenero(@PathVariable String genero) {
        List<Ejemplar> ejemplares = ejemplarServicio.buscarPorTema(genero);
        boolean existe = ejemplares != null && !ejemplares.isEmpty();

        if (existe) {
            return new ResponseEntity<>(ejemplares, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    

    @PostMapping
    public ResponseEntity<Ejemplar> crearEjemplar(@RequestBody Ejemplar ejemplar) {
        Ejemplar nuevoEjemplar = ejemplarRepositorio.save(ejemplar);
        return new ResponseEntity<>(nuevoEjemplar, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ejemplar> actualizarEjemplar(@PathVariable int id, @RequestBody Ejemplar ejemplar) {
        Optional<Ejemplar> ejemplarExistente = ejemplarRepositorio.findById(id);
        boolean existe = ejemplarExistente.isPresent();

        if (existe) {
            Ejemplar ejemplarActualizado = ejemplarExistente.get();
            String titulo = ejemplar.getTitulo();
            String estado = ejemplar.getEstado();
            String categoria = ejemplar.getCategoria();
            String estadoFisico = ejemplar.getEstadoFisico();

            if (titulo != null) {
                ejemplarActualizado.setTitulo(titulo);
            }
            if (estado != null) {
                ejemplarActualizado.setEstado(estado);  //Esto lo debe corroborar javascript
            }
            if (categoria != null) {
                ejemplarActualizado.setCategoria(categoria);
            }
            if (estadoFisico != null) {
                ejemplarActualizado.setEstadoFisico(estadoFisico);
            }

            Ejemplar ejemplarGuardado = ejemplarRepositorio.save(ejemplarActualizado);
            return new ResponseEntity<>(ejemplarGuardado, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarEjemplar(@PathVariable int id) {
        try {
            ejemplarRepositorio.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
           return ResponseEntity.notFound().build();
        }
    }


    
    

}
