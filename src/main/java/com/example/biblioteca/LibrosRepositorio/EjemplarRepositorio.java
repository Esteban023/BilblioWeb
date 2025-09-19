package com.example.biblioteca.LibrosRepositorio;

import com.example.biblioteca.Model.Ejemplar;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository

public interface EjemplarRepositorio extends JpaRepository<Ejemplar, Integer> {
    
}
