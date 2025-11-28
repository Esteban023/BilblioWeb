package com.example.biblioteca.bibliotecaRepositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.biblioteca.Model.Usuario;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer> {
    @Query("SELECT user FROM Usuario user WHERE user.cedula = ?1")
    Optional<Usuario> encontarPorCedula(Long cedula);

    @Query(value = """
        SELECT user.* FROM Usuario user
        JOIN reserva r ON user.id = r.id_usuario
        WHERE r.codigo_de_barras = :codigoDeBarras AND r.posicion_cola = 1    
            """, nativeQuery = true)
    Optional<Usuario> findPorReservaCodDeBarras(
        @Param("codigoDeBarras") String codigoDeBarras);
}
