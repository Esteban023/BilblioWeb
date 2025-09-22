package com.example.biblioteca.Controlador;

import java.util.List;
import org.springframework.http.HttpStatus;
import com.example.biblioteca.Model.Usuario;
import org.springframework.http.ResponseEntity;
import com.example.biblioteca.Servicicos.UsuarioServicio;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/usuarios")

public class UsuariosControlador {
    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios(@RequestParam(required = false) String param) {
        List<Usuario> usuarios = usuarioServicio.listarUsuarios();
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@RequestParam int id) {
        Usuario usuario = usuarioServicio.obtenerUsuarioPorId(id).orElse(null);

        if (usuario != null) {
            return new ResponseEntity<>(usuario, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario usuario) {
        Usuario nuevoUsuario = usuarioServicio.guardarUsuario(usuario);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    @PutMapping("path/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@RequestParam int id, @RequestBody Usuario usuario) {
        Usuario usuarioExistente = usuarioServicio.obtenerUsuarioPorId(id).orElse(null);

        if (usuarioExistente != null) {
            Usuario usuarioActualizado = usuarioServicio.actualizarUsuario(id, usuario);
            return new ResponseEntity<>(usuarioActualizado, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("path/{id}")
    public ResponseEntity<Void> borrarUsuario(@RequestParam int id) {
        Usuario usuarioExistente = usuarioServicio.obtenerUsuarioPorId(id).orElse(null);

        if (usuarioExistente != null) {
            usuarioServicio.eliminarUsuario(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
