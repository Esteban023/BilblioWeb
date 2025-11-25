package com.example.biblioteca.Controlador;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.biblioteca.Model.Libros;
import com.example.biblioteca.Servicicos.LibrosServicios;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/libros")

public class LibrosControlador {
    @Autowired
    private LibrosServicios librosServicios;

    @GetMapping
    public ResponseEntity<List<Libros>> listarLibros(@RequestParam(required = false) String titulo) {
        List<Libros> libros = librosServicios.listarLibros();
        return new ResponseEntity<>(libros, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Libros> obtenerLibroPorId(@PathVariable int id) {
        Optional <Libros> libro = librosServicios.obtenerLibroPorId(id);

        boolean isPresent = libro.isPresent();
        if (isPresent) {
            return new ResponseEntity<>(libro.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
    }

    @PostMapping
    public ResponseEntity<Libros> crearLibro(@RequestBody Libros libro) {
        Libros nuevoLibro = librosServicios.guardarLibro(libro);
        return new ResponseEntity<>(nuevoLibro, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Libros> actualizarLibro(@PathVariable int id, @RequestBody Libros libro) {
        Optional<Libros> libroExistente = librosServicios.obtenerLibroPorId(id);
        boolean existe = libroExistente.isPresent();

        if (existe) {
            Libros libroActualizado = libroExistente.get();
            String titulo = libro.getTitulo();
            String genero = libro.getGenero();
            BigDecimal precio = libro.getPrecio();
            String editorial = libro.getEditorial();
            LocalDate fechaEdicion = libro.getFechaEdicion();

            libroActualizado.setTitulo(titulo);
            libroActualizado.setGenero(genero);
            libroActualizado.setPrecio(precio);
            libroActualizado.setFechaEdicion(fechaEdicion);
            libroActualizado.setEditorial(editorial);
            return new ResponseEntity<>(librosServicios.guardarLibro(libro), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarLibro(@PathVariable int id) {
        try {
            librosServicios.eliminarLibro(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
           return ResponseEntity.notFound().build();
        }
    }
    
    
    
}
