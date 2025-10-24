package com.example.biblioteca.bibliotecaRepositorio;

import java.util.List;
import java.util.Optional;
import com.example.biblioteca.Model.Prestamo;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PrestamoRepositorio extends JpaRepository<Prestamo, Integer> {
    @Modifying
    @Transactional
    @Query("UPDATE Prestamo p SET p.estado = false WHERE p.id = ?1")
    Optional<Prestamo> finalizarPrestamo(Integer prestamoId);

    List<Prestamo> findByUsuarioId(Integer usuarioId);
}
