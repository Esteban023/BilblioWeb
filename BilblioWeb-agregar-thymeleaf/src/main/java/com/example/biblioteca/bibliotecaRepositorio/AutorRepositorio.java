package com.example.biblioteca.bibliotecaRepositorio;

import com.example.biblioteca.Model.Autor;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface AutorRepositorio extends JpaRepository<Autor, Integer> {}
