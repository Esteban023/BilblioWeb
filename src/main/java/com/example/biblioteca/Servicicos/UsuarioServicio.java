package com.example.biblioteca.Servicicos;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.biblioteca.bibliotecaRepositorio.UsuarioRepositorio;
import com.example.biblioteca.Model.Usuario;

@Service
public class UsuarioServicio {
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    public List<Usuario> listarUsuarios() {
        return usuarioRepositorio.findAll();
    }

    public Optional<Usuario> obtenerUsuarioPorId(Integer id) {
        return usuarioRepositorio.findById(id);
    }

    public Optional<Usuario> obtenerUsuarioPorCedula(Long cedula) {
        return usuarioRepositorio.findByCedula(cedula);
    }

    public Usuario guardarUsuario(Usuario usuario) {
        return usuarioRepositorio.save(usuario);
    }

    public void eliminarUsuario(Integer id) {
        usuarioRepositorio.deleteById(id);
    }

    public Usuario actualizarUsuario(int id, Usuario usuario) {
        Usuario usuarioExistente = usuarioRepositorio.findById(id).orElseThrow(
            () -> {
                throw new RuntimeException("Usuario no encontrado con id: " + id);
            }
        );

        return usuarioRepositorio.save(usuarioExistente);
    }

}
