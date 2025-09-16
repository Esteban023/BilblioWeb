package com.example.biblioteca.LibrosRepositorio;

import com.example.biblioteca.Model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface AutorRepositorio extends JpaRepository<Autor, Integer> {

}
