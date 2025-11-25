package com.example.biblioteca.bibliotecaRepositorio;

import java.util.Optional;
import com.example.biblioteca.Model.Usuario;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer> {
    @Query("SELECT user FROM Usuario user WHERE user.cedula = ?1")
    Optional<Usuario> encontarPorCedula(Long cedula);
}
