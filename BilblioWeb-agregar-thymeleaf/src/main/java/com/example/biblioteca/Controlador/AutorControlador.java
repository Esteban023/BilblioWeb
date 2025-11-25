package com.example.biblioteca.Controlador;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.biblioteca.Model.Autor;
import com.example.biblioteca.Servicicos.AutorServicio;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/autores")

public class AutorControlador {
    @Autowired
    private AutorServicio autorServicio;

    @GetMapping
    public ResponseEntity<List<Autor>> listarAutores(@RequestParam(required = false) String nombre) {
        List<Autor> autores = autorServicio.listarAutores();
        return new ResponseEntity<>(autores, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Autor> obtenerAutorPorId(@PathVariable int id) {
        Optional<Autor> autor = autorServicio.obtenerAutorPorId(id);
        boolean existe = autor.isPresent();
        
        if (existe) {
            return new ResponseEntity<>(autor.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<Autor> crearAutor(@RequestBody Autor autor) {
        Autor nuevoAutor = autorServicio.guardarAutor(autor);
        return new ResponseEntity<>(nuevoAutor, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Autor> actualizarAutor(@PathVariable int id, @RequestBody Autor autor) {
        Optional<Autor> autorExistente = autorServicio.obtenerAutorPorId(id);
        boolean existe = autorExistente.isPresent();

        if (existe) {
            Autor autorActualizado = autorExistente.get();
            String nombre = autor.getNombre();
            String apellido = autor.getApellido();
            String telefono = autor.getTelefono();

            autorActualizado.setNombre(nombre);
            autorActualizado.setApellido(apellido);
            autorActualizado.setTelefono(telefono);
            return new ResponseEntity<>(autorServicio.guardarAutor(autor), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarAutor(@PathVariable int id) {
        try {
            autorServicio.eliminarAutor(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
           return ResponseEntity.notFound().build();
        }
    }
    

}
