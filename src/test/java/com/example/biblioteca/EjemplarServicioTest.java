package com.example.biblioteca;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.biblioteca.Model.Ejemplar;
import com.example.biblioteca.Servicicos.EjemplarServicio;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class EjemplarServicioTest {

    @Autowired
    private EjemplarServicio ejemplarServicio;

    List<Ejemplar> ejemplares;

    List<Ejemplar> creaEjemplares() {

        Object [][] ejemplares = {
            {11, "GENERAL", "Disponible", "Bueno", "¿Sueñan los androides con ovejas eléctricas?", 21},
            {12, "RESERVA" ,"Prestado", "Regular", "Cien años de soledad", 1},
            {13, "GENERAL", "Disponible", "Malo", "La casa de los espíritus", 2},
            {14, "RESERVA", "Prestado", "Bueno", "El amor en los tiempos del cólera", 1},
            {15, "GENERAL", "Disponible", "Regular", "Crónica de una muerte anunciada", 1}
        };

        List<Ejemplar> ejs = new java.util.ArrayList<>(5);

        for (int i = 0; i < 5; i++) {
            Ejemplar ejemplar = new Ejemplar();
            
            Integer id = (Integer) ejemplares[i][0];
            String categoria = (String) ejemplares[i][1];
            String estado = (String) ejemplares[i][2];
            String estadoFisico = (String) ejemplares[i][3];
            String titulo = (String) ejemplares[i][4];
            Integer autor_id = (Integer) ejemplares[i][5];

            ejemplar.setId(id);
            ejemplar.setCategoria(categoria);
            ejemplar.setEstado(estado);
            ejemplar.setEstadoFisico(estadoFisico);
            ejemplar.setTitulo(titulo);
            ejemplar.setIdAutor(autor_id);

            ejs.add(ejemplar);
        }

        return ejs;
    }

    @Test
    void guardarEjemplar() {
        for (Ejemplar ejemplar : ejemplares) {
            Ejemplar ejemplarGuardado = ejemplarServicio.guardarEjemplar(ejemplar);
            assertNotNull(ejemplarGuardado);
            assert(ejemplarGuardado.getId().equals(ejemplar.getId()));
            assert(ejemplarGuardado.getTitulo().equals(ejemplar.getTitulo()));
            assert(ejemplarGuardado.getEstado().equals(ejemplar.getEstado()));
            assert(ejemplarGuardado.getCategoria().equals(ejemplar.getCategoria()));
            assert(ejemplarGuardado.getEstadoFisico().equals(ejemplar.getEstadoFisico()));
            assert(ejemplarGuardado.getIdAutor().equals(ejemplar.getIdAutor()));
        }
    }   

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        ejemplares = creaEjemplares();
        guardarEjemplar();
    }

    @Test
    void listarEjemplares() {
        List<Ejemplar> ejemplaresListados = ejemplarServicio.listarEjemplares();
        assertNotNull(ejemplaresListados);
        assert(ejemplaresListados.size() > 0);
    }

    @Test
    void obtenerEjemplarPorId() {
        for (Ejemplar ejemplar : ejemplares) {
            Optional<Ejemplar> ejemplarObtenido = ejemplarServicio.obtenerEjemplarPorId(ejemplar.getId());
            assertNotNull(ejemplarObtenido);
            assert(ejemplarObtenido.get().getId().equals(ejemplar.getId()));
            assert(ejemplarObtenido.get().getTitulo().equals(ejemplar.getTitulo()));
            assert(ejemplarObtenido.get().getEstado().equals(ejemplar.getEstado()));
            assert(ejemplarObtenido.get().getCategoria().equals(ejemplar.getCategoria()));
            assert(ejemplarObtenido.get().getEstadoFisico().equals(ejemplar.getEstadoFisico()));
            assert(ejemplarObtenido.get().getIdAutor().equals(ejemplar.getIdAutor()));
        }
    }

    @Test
    void eliminarEjemplar() {
        for (Ejemplar ejemplar : ejemplares) {
            Integer id = ejemplar.getId();
            ejemplarServicio.eliminarEjemplar(id);
            Optional<Ejemplar> ejemplarEliminado = ejemplarServicio.obtenerEjemplarPorId(id);
            assert(ejemplarEliminado.isEmpty());
        }
    }

    @Test
    void actualizarEjemplar() {
        for (Ejemplar ejemplar : ejemplares) {
            Integer id = ejemplar.getId();
            Optional<Ejemplar> ejemplarExistente = ejemplarServicio.obtenerEjemplarPorId(id);
            assert(ejemplarExistente.isPresent());

            Ejemplar ejActualizado = ejemplarExistente.get();
            ejActualizado.setEstado("Prestado");
            ejActualizado.setEstadoFisico("Regular");

            Ejemplar ejemplarActualizado = ejemplarServicio.guardarEjemplar(ejActualizado);
            assertNotNull(ejemplarActualizado);
            assert(ejemplarActualizado.getId().equals(ejActualizado.getId()));
            assert(ejemplarActualizado.getTitulo().equals(ejActualizado.getTitulo()));
            assert(ejemplarActualizado.getEstado().equals("Prestado"));
            assert(ejemplarActualizado.getCategoria().equals(ejActualizado.getCategoria()));
            assert(ejemplarActualizado.getEstadoFisico().equals("Regular"));
            assert(ejemplarActualizado.getIdAutor().equals(ejActualizado.getIdAutor()));
        }
    }

    @Test
    void buscarPorPalabraClave() {
        String palabraClave = "Cien";

        List<Ejemplar> resultados = ejemplarServicio.buscarPorPalabraClave(palabraClave);
        assertNotNull(resultados);
        assert(resultados.size() > 0);

        for (Ejemplar ejemplar : resultados) {
            assert(ejemplar.getTitulo().contains(palabraClave) || ejemplar.getCategoria().contains(palabraClave));
        }
    }

    @Test
    void buscarPorTituloYautor() {
        String titulo = "Cien años de soledad";
        String nombreAutor = "Gabriel";

        List<Ejemplar> resultados = ejemplarServicio.buscarPorTituloAutor(titulo, nombreAutor);
        assertNotNull(resultados);
        assert(resultados.size() > 0);

        for (Ejemplar ejemplar : resultados) {
            assert(ejemplar.getTitulo().equals(titulo) || ejemplar.getAutor().getNombre().contains(nombreAutor));
        }
    }

    @Test
    void buscarPorTema() {
        String tema = "Literatura";

        List<Ejemplar> resultados = ejemplarServicio.buscarPorTema(tema);
        assertNotNull(resultados);
        assert(resultados.size() > 0);

        for (Ejemplar ejemplar : resultados) {
            assert(ejemplar.getTema().equals(tema));
        }
    }
}
