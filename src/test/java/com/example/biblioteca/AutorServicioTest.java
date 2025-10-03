package com.example.biblioteca;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.biblioteca.Model.Autor;
import com.example.biblioteca.Servicicos.AutorServicio;
import com.example.biblioteca.bibliotecaRepositorio.AutorRepositorio;

import java.util.Optional;

@SpringBootTest
@Transactional
public class AutorServicioTest {

    private List<Autor> autores;

    @Autowired
    private AutorServicio autorServicio;

    @Autowired
    private AutorRepositorio autorRepositorio;

    
    
    public List<Autor> creaAutores() {

        Object [][] autores = {
            {"Gabriel", "García Márquez", "3002034567"}, 
            {"Isabel", "Allende", "3003045678"},
            {"Mario" ,"Vargas Llosa", "3004056789"},
            {"Jorge", "Luis Borges", "3005067890"},
            {"Pablo", "Neruda", "3006078901"}
        };

        List<Autor> auts = new java.util.ArrayList<>(5);

        for (int i = 0; i < 5; i++) {
            Autor autor = new Autor();
            
            String nombre = (String) autores[i][0];
            String apellido = (String) autores[i][1];
            String telefono = (String) autores[i][2];

            autor.setNombre(nombre);
            autor.setApellido(apellido);
            autor.setTelefono(telefono);

            auts.add(autor);
        }

        return auts;
    }

    @BeforeEach
    void setUp() {
        autores = creaAutores();
        guardarUsuario();
    }

    @Test
    void guardarUsuario() {
        for (Autor autor : autores) {
            Autor autorGuardado = autorServicio.guardarAutor(autor);

            assertNotNull(autorGuardado);
            assert(autorGuardado.getId().equals(autor.getId()));
            assert(autorGuardado.getNombre().equals(autor.getNombre()));
            assert(autorGuardado.getApellido().equals(autor.getApellido()));
            assert(autorGuardado.getTelefono().equals(autor.getTelefono()));

        }
    }

    @Test
    void buscarUsuarioPorId() {
        for (Autor autor : autores) {
            Integer id = autor.getId();
            Optional<Autor> autorBuscado = autorServicio.obtenerAutorPorId(id);
            //Assert
            assert(autorBuscado.isPresent());
            assert(autorBuscado.get().getId().equals(autor.getId()));
            assert(autorBuscado.get().getNombre().equals(autor.getNombre()));
            assert(autorBuscado.get().getApellido().equals(autor.getApellido()));
            assert(autorBuscado.get().getTelefono().equals(autor.getTelefono()));
        }
    }

    @Test
    void listarUsuarios() {
        List<Autor> autoresListados = autorServicio.listarAutores();
        assertNotNull(autoresListados);
        assert(autoresListados.size() > 0);
    }

    @Test
    void eliminarUsuario() {
        for (Autor autor : autores) {
            Integer id = autor.getId();
            autorServicio.eliminarAutor(id);
            Optional<Autor> autorEliminado = autorServicio.obtenerAutorPorId(id);
            assert(autorEliminado.isEmpty());
        }
    }

    @Test
    void actualizarAutor() {
        for (Autor autor : autores) {
            Integer id = autor.getId();
            autor.setNombre(autor.getNombre() + " Actualizado");
            autor.setApellido(autor.getApellido() + " Actualizado");
            autor.setTelefono(autor.getTelefono());

            Autor autorActualizado = autorServicio.actualizarAutor(id, autor);
            assertNotNull(autorActualizado);
            assert(autorActualizado.getId().equals(autor.getId()));
            assert(autorActualizado.getNombre().equals(autor.getNombre()));
            assert(autorActualizado.getApellido().equals(autor.getApellido()));
            assert(autorActualizado.getTelefono().equals(autor.getTelefono()));
        }
    }

    @Test
    void eliminarAutor() {
        // Setup
        Integer id = autores.get(0).getId();
        Optional<Autor> autorAntes = autorRepositorio.findById(id);

        assert(autorAntes.isPresent());
        autorServicio.eliminarAutor(id);

        // Verificación
        Optional<Autor> autorEliminado = autorRepositorio.findById(id);
        assert(autorEliminado.isEmpty());

    }

    @AfterEach
    void tearDown() {
        for (Autor autor : autores) {
            Integer id = autor.getId();
            Optional<Autor> autorExistente = autorRepositorio.findById(id);
            if (autorExistente.isPresent()) {
                autorServicio.eliminarAutor(id);
            }
        }
        autores.clear();
    }



    


}
