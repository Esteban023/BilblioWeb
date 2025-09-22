package com.example.biblioteca.bibliotecaRepositorio;

import com.example.biblioteca.Model.Libros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibrosRepositorio extends JpaRepository<Libros, Integer> {

}
