package com.example.biblioteca.bibliotecaRepositorio;

import java.util.Optional;
import com.example.biblioteca.Model.Usuario;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, com.example.biblioteca.Model.UsuarioId> {
    
    void deleteById(Integer id);
    void deleteByCedula(Long cedula);
    Optional<Usuario> findById(Integer id);
    Optional<Usuario> findByCedula(Long cedula);
    Optional<Usuario> findByEmail(String email);

    @Query("DELETE FROM Usuario u WHERE u.cedula = ?1 AND u.id = ?2")
    void deleteByPrimaryKey(Long cedula, Integer id);

    @Query("SELECT u FROM Usuario u WHERE u.cedula = ?1 AND u.id = ?2")
    Optional<Usuario> findByPrimaryKey(Long cedula, Integer id);
    
}
