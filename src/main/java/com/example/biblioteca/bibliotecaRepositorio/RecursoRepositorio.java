package com.example.biblioteca.bibliotecaRepositorio;

import com.example.biblioteca.Model.RecursoBibliografico;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface RecursoRepositorio extends JpaRepository<RecursoBibliografico, String> {
    
       @Query(value = "SELECT DISTINCT r.* FROM recurso_bibliografico r " +
              "LEFT JOIN autores_recursos_bibliograficos ra ON r.codigo_de_barras = ra.codigo_de_barras " +
              "LEFT JOIN autor a ON ra.autor_id = a.id " +
              "WHERE LOWER(r.titulo) LIKE LOWER(CONCAT('%', ?1, '%')) " +
              "OR LOWER(a.nombre) LIKE LOWER(CONCAT('%', ?1, '%')) " +
              "OR LOWER(a.apellido) LIKE LOWER(CONCAT('%', ?1, '%')) " +
              "OR LOWER(r.publicacion) LIKE LOWER(CONCAT('%', ?1, '%')) " +
              "OR LOWER(r.descripcion) LIKE LOWER(CONCAT('%', ?1, '%')) " +
              "OR LOWER(r.tipo_de_publicacion) LIKE LOWER(CONCAT('%', ?1, '%')) " +
              "OR LOWER(r.idioma) LIKE LOWER(CONCAT('%', ?1, '%')) " +
              "OR LOWER(r.tema) LIKE LOWER(CONCAT('%', ?1, '%')) " +
              "OR LOWER(r.codigo_de_barras) LIKE LOWER(CONCAT('%', ?1, '%')) " +
              "OR LOWER(r.localizacion) LIKE LOWER(CONCAT('%', ?1, '%')) " +
              "OR LOWER(r.estante) LIKE LOWER(CONCAT('%', ?1, '%')) " +
              "OR LOWER(r.signatura_tipografica) LIKE LOWER(CONCAT('%', ?1, '%')) " +
              "OR LOWER(r.coleccion) LIKE LOWER(CONCAT('%', ?1, '%')) " +
              "OR LOWER(r.estado) LIKE LOWER(CONCAT('%', ?1, '%')) " +
              "OR LOWER(r.categoria) LIKE LOWER(CONCAT('%', ?1, '%')) " +
              "OR LOWER(r.estado_fisico) LIKE LOWER(CONCAT('%', ?1, '%')) " +
              "OR LOWER(r.contenido) LIKE LOWER(CONCAT('%', ?1, '%'))", 
       nativeQuery = true)
       List<RecursoBibliografico> buscarPorPalabraClave(String palabraClave);

       @Modifying
       @Query("DELETE FROM RecursoBibliografico recurso WHERE recurso.isbn = ?1")
       void deleteByIsbn(String isbn);
       
       @Query("SELECT recurso FROM RecursoBibliografico recurso WHERE recurso.isbn = ?1")
       Optional<RecursoBibliografico> buscarPorIsbn(String isbn);

       @Query("SELECT r FROM RecursoBibliografico r " +
              "LEFT JOIN r.autores a " +
              "WHERE r.titulo ILIKE CONCAT('%', ?1, '%') " +
              "OR a.nombre ILIKE CONCAT('%', ?2, '%')" +
              "OR a.apellido ILIKE CONCAT('%', ?2, '%')"
       )   
       Set<RecursoBibliografico> buscarRecursoBibliograficoPorTituloNombreAutor(
              String titulo, 
              String nombreAutor
       );
       
       @Query("SELECT recurso FROM RecursoBibliografico recurso WHERE recurso.tema ILIKE CONCAT('%', ?1, '%')")
       Set<RecursoBibliografico> encontrarPorTema(String tema);

       
}
