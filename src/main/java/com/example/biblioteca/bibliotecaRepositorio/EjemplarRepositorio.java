package com.example.biblioteca.bibliotecaRepositorio;

import com.example.biblioteca.Model.Ejemplar;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface EjemplarRepositorio extends JpaRepository<Ejemplar, Integer> {
    
    @Query("SELECT e FROM Ejemplar e JOIN e.autor a " +
           "WHERE LOWER(e.titulo) LIKE LOWER(CONCAT('%', ?1, '%')) " +
           "OR LOWER(e.descripcion) LIKE LOWER(CONCAT('%', ?1, '%')) " +
           "OR LOWER(a.nombre) LIKE LOWER(CONCAT('%', ?1, '%')) " +
           "OR LOWER(a.apellido) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<Ejemplar> buscarPorPalabraClave(String palabraClave);

    @Query("SELECT ejemplar FROM Ejemplar ejemplar WHERE ejemplar.titulo ILIKE CONCAT('%', ?1, '%')" +
           "OR ejemplar.autor.nombre ILIKE CONCAT('%',?2,'%')") 
    List<Ejemplar> buscarLibroPorTituloNombreAutor(String titulo, String nombreAutor);

    @Query("SELECT e FROM Ejemplar e WHERE e.tema ILIKE CONCAT('%', ?1, '%')")
    List<Ejemplar> findByTema(String tema);
}
