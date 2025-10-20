package com.example.biblioteca;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import com.example.biblioteca.Servicicos.AutorServicio;
import jakarta.transaction.Transactional;
import com.example.biblioteca.Model.Autor;
import com.example.biblioteca.Model.RecursoBibliografico;
import com.example.biblioteca.Servicicos.RecursoServicio;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;

@SpringBootTest
@Transactional
public class RecursoBiblioTest {

    @Autowired
    private RecursoServicio recursoServicio;

    @Autowired
    private AutorServicio autorServicio;

    private Set<Autor> autores;
    private Set<RecursoBibliografico> recursosBibliograficos;

    // MÉTODOS PROVEEDORES
    private static Stream<Arguments> proveedorPalabrasClave() {
        return Stream.of(
            Arguments.of("recurso", 3),
            Arguments.of("Coleccion", 3),
            Arguments.of("Tema1", 1)
        );
    }

    @BeforeEach
    void setUp() {
        autores = crearAutores();
        recursosBibliograficos = crearRecursos(autores);
        insertarRecursos(recursosBibliograficos);
    }

    Set<Autor> crearAutores() {

        Set<Autor> autores = new HashSet<>(3);
        for (int i = 1; i <= 3; i++) {
            Autor autor = new Autor();
            autor.setNombre("Nombre" + i);
            autor.setApellido("Apellido" + i);
            autor.setNacionalidad("Nacionalidad" + i);
            autor.setTelefono("123456789" + i);
            autores.add(autor);
        }
        
        return autores;
    }

    Set<RecursoBibliografico> crearRecursos(Set<Autor> autores) {
        Set<RecursoBibliografico> recursos = new HashSet<>(3);

        for (int i = 1; i <= 3; i++) {
            RecursoBibliografico recurso = new RecursoBibliografico();
            recurso.setCodigoDeBarras("CB" + i);
            recurso.setCalificacion(String.valueOf(3 + i % 3)); // Calificación entre 3 y 5
            recurso.setCategoria("Categoría " + i);
            recurso.setColeccion("Colección " + i);
            recurso.setComentarios("Comentario del recurso " + i);
            recurso.setContenido("Contenido " + i);
            recurso.setDescripcion("Descripcion del recurso " + i);
            recurso.setEstado(i % 2 == 0 ? "Disponible" : "Prestado");
            recurso.setEstadoFisico(i % 3 == 0 ? "Regular" : "Bueno");
            recurso.setEstante("A" + i);
            recurso.setIdioma(i % 2 == 0 ? "Español" : "Inglés");
            recurso.setIsbn("ISBN" + i);
            recurso.setLocalizacion("Sección " + i);
            recurso.setNotas("Notas " + i);
            recurso.setPublicacion("202" + (4 - i));
            recurso.setResumen("Resumen del recurso " + i);
            recurso.setSignaturaTipografica("ST" + i);
            recurso.setTema("Tema" + i);
            recurso.setTipoDePublicacion(i % 2 == 0 ? "Libro" : "Revista");
            recurso.setTitulo("Titulo" + i);
            recurso.setAutores(autores);
            // Asignar un autor aleatorio del conjunto de autores
            recursos.add(recurso);
        }
        return recursos;
    }

    @Test
    private void insertarRecursos(Set<RecursoBibliografico> recursos) {
        for (RecursoBibliografico rb : recursos) {
            RecursoBibliografico recursoGuardado = recursoServicio.guardarRecursoBibliografico(rb);
            Assertions.assertNotNull(recursoGuardado, "El recurso guardado no debe ser nulo");
        }
    }

    @Test
    void testInsertarRecursoBibliografico() {
        RecursoBibliografico recurso = new RecursoBibliografico();
        recurso.setCodigoDeBarras("CB100");
        recurso.setCalificacion("5");
        recurso.setCategoria("Categoría Test");
        recurso.setColeccion("Colección Test");
        recurso.setComentarios("Comentario de prueba");
        recurso.setContenido("Contenido de prueba");
        recurso.setDescripcion("Descripción de prueba");
        recurso.setEstado("Disponible");
        recurso.setEstadoFisico("Excelente");
        recurso.setEstante("B1");
        recurso.setIdioma("Inglés");
        recurso.setNotas("Teste notas");
        recurso.setIsbn("ISBN100");
        recurso.setLocalizacion("Sección Test");
        recurso.setPublicacion("2024");
        recurso.setResumen("Resumen de prueba");
        recurso.setSignaturaTipografica("ST100");
        recurso.setTema("Tema Test");
        recurso.setTipoDePublicacion("Libro");
        recurso.setTitulo("Título de prueba");
        recurso.setAutores(autores);
        
        // Act
        RecursoBibliografico recursoGuardado = recursoServicio.guardarRecursoBibliografico(recurso);
        
        // Assert
        assertNotNull(recursoGuardado, "El recurso guardado no debe ser nulo");
        assertAll(
            "Verificación de todos los campos del recurso bibliográfico",
            () -> assertEquals(recurso.getCodigoDeBarras(), recursoGuardado.getCodigoDeBarras(), "Código de barras debe coincidir"),
            () -> assertEquals(recurso.getCalificacion(), recursoGuardado.getCalificacion(), "Calificación debe coincidir"),
            () -> assertEquals(recurso.getCategoria(), recursoGuardado.getCategoria(), "Categoría debe coincidir"),
            () -> assertEquals(recurso.getColeccion(), recursoGuardado.getColeccion(), "Colección debe coincidir"),
            () -> assertEquals(recurso.getComentarios(), recursoGuardado.getComentarios(), "Comentarios deben coincidir"),
            () -> assertEquals(recurso.getContenido(), recursoGuardado.getContenido(), "Contenido debe coincidir"),
            () -> assertEquals(recurso.getDescripcion(), recursoGuardado.getDescripcion(), "Descripción debe coincidir"),
            () -> assertEquals(recurso.getEstado(), recursoGuardado.getEstado(), "Estado debe coincidir"),
            () -> assertEquals(recurso.getEstadoFisico(), recursoGuardado.getEstadoFisico(), "Estado físico debe coincidir"),
            () -> assertEquals(recurso.getEstante(), recursoGuardado.getEstante(), "Estante debe coincidir"),
            () -> assertEquals(recurso.getIdioma(), recursoGuardado.getIdioma(), "Idioma debe coincidir"),
            () -> assertEquals(recurso.getIsbn(), recursoGuardado.getIsbn(), "ISBN debe coincidir"),
            () -> assertEquals(recurso.getLocalizacion(), recursoGuardado.getLocalizacion(), "Localización debe coincidir"),
            () -> assertEquals(recurso.getNotas(), recursoGuardado.getNotas(), "Notas deben coincidir"),
            () -> assertEquals(recurso.getPublicacion(), recursoGuardado.getPublicacion(), "Publicación debe coincidir"),
            () -> assertEquals(recurso.getResumen(), recursoGuardado.getResumen(), "Resumen debe coincidir"),
            () -> assertEquals(recurso.getSignaturaTipografica(), recursoGuardado.getSignaturaTipografica(), "Signatura tipográfica debe coincidir"),
            () -> assertEquals(recurso.getTema(), recursoGuardado.getTema(), "Tema debe coincidir"),
            () -> assertEquals(recurso.getTipoDePublicacion(), recursoGuardado.getTipoDePublicacion(), "Tipo de publicación debe coincidir"),
            () -> assertEquals(recurso.getTitulo(), recursoGuardado.getTitulo(), "Título debe coincidir")
        );

    }

    @ParameterizedTest
    @MethodSource("proveedorPalabrasClave")
    void testBuscarPorPalabraClave(String palabraClave, int resultadosEsperados) {
        List<RecursoBibliografico> resultado = recursoServicio.buscarPorPalabraClave(palabraClave);
        assertEquals(resultadosEsperados, resultado.size(), "El número de resultados debe coincidir con el esperado");
    }

    @Test
    void testBuscarPorTema(){
        Set<RecursoBibliografico> resultado = recursoServicio.buscarPorTema("Tema");
        assertEquals(4, resultado.size(), "Solo hay 4 datos de prueba que contienen la palabra Tema");
    }


    @Test
    void testBuscarPorTituloYAutor() {
        Set<RecursoBibliografico> resultado = recursoServicio.buscarPorTituloAutor("titulo1", "Nombre1");
        assert(resultado.size() > 0);
    }

    


    @AfterEach
    void limpiarDatos() {
        recursosBibliograficos.forEach(recurso -> {
            recursoServicio.eliminarRecursoBibliografico(
                recurso.getCodigoDeBarras()
            );
            }
        );
        recursosBibliograficos.clear();
    }

}
