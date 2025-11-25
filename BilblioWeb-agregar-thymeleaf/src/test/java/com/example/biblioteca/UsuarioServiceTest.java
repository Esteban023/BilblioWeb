package com.example.biblioteca;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.biblioteca.Model.Usuario;
import com.example.biblioteca.Servicicos.UsuarioServicio;
import com.example.biblioteca.bibliotecaRepositorio.UsuarioRepositorio;

@SpringBootTest
@Transactional
public class UsuarioServiceTest {

    private List<Usuario> usuarios;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    List<Usuario> creaUsuarios() {

        Object [][] usuarios = {
                {11, 2034567890L, "Gómez", "juan.gomez@email.com", true, "Juan Manuel", "juan123", "ADMIN", "3002034567" }, 
                {12, 3045678901L, "López", "Diego.@gmail.com", false, "Diego", "diego_456", "ESTUDIANTE", "3003045678" },
                {13, 4056789012L, "Martínez", "Santiago.martinez@email.com", true, "Santiago", "santi789", "BIBLIOTECARIO", "3004056789" },
                {14, 5067890123L, "Hernández", "JuanPablo@email.com", false, "Juan Pablo", "juanpablo101", "ESTUDIANTE", "3005067890" },
                {15, 6078901234L, " García", "Juan.@email,com", true, "Juan", "juan202", "ADMIN", "3006078901" }
        };

        List<Usuario> users = new ArrayList<>(5);

        for (int i = 0; i < 5; i++) {
            Usuario user = new Usuario();
            
            Integer id = (Integer) usuarios[i][0];
            Long cedula = (Long) usuarios[i][1];
            String apellido = (String) usuarios[i][2];
            String email = (String) usuarios[i][3];
            Boolean estado = (Boolean) usuarios[i][4];
            String nombre = (String) usuarios[i][5];
            String password = (String) usuarios[i][6];
            String rol = (String) usuarios[i][7];
            String telefono = (String) usuarios[i][8];

            user.setId(id);
            user.setCedula(cedula);
            user.setApellido(apellido);
            user.setEmail(email);
            user.setEstado(estado);
            user.setNombre(nombre);
            user.setPassword(password);
            user.setRol(rol);
            user.setTelefono(telefono);
            users.add(user);
        }
                
        return users;
    }

    @BeforeEach
    void setUp() {
        usuarios = creaUsuarios();
        guardarUsuario();
    }

    @Test
    void contextLoads() {
        // Verifica que el contexto de Spring se cargue correctamente
        assert(usuarioServicio != null);
        assert(usuarioRepositorio != null);
    }

    @Test
    void guardarUsuario() {
        for (Usuario user : usuarios) {
            Usuario usuarioGuardado = usuarioServicio.guardarUsuario(user);
            assertNotNull(user);
            assert(usuarioGuardado.getId().equals(user.getId()));
            assert(usuarioGuardado.getNombre().equals(user.getNombre()));
            assert(usuarioGuardado.getApellido().equals(user.getApellido()));
            assert(usuarioGuardado.getCedula().equals(user.getCedula()));
            assert(usuarioGuardado.getEmail().equals(user.getEmail()));
            assert(usuarioGuardado.getEstado() == user.getEstado());
            assert(usuarioGuardado.getPassword().equals(user.getPassword()));
            assert(usuarioGuardado.getRol().equals(user.getRol()));
            assert(usuarioGuardado.getTelefono().equals(user.getTelefono()));
        }
    }

    @Test
    void buscarUsuarioPorId() {

        for (Usuario user : usuarios) {
            Integer id = user.getId();
            Optional<Usuario> usuarioBuscado = usuarioServicio.obtenerUsuarioPorId(id);
            //Assert
            assert(usuarioBuscado.isPresent());
            assert(usuarioBuscado.get().getId().equals(user.getId()));
            assert(usuarioBuscado.get().getNombre().equals(user.getNombre()));
            assert(usuarioBuscado.get().getApellido().equals(user.getApellido()));
            assert(usuarioBuscado.get().getCedula().equals(user.getCedula()));
            assert(usuarioBuscado.get().getEmail().equals(user.getEmail()));
            assert(usuarioBuscado.get().getEstado() == user.getEstado());
            assert(usuarioBuscado.get().getPassword().equals(user.getPassword()));
            assert(usuarioBuscado.get().getRol().equals(user.getRol()));
            assert(usuarioBuscado.get().getTelefono().equals(user.getTelefono()));
        }

    }

    @Test
    void testEliminarUsuario() {
        // Setup
        Usuario usuario = usuarios.get(0);
        Integer id = usuario.getId();

        Optional<Usuario> usuarioAntes = usuarioRepositorio.findById(id);
        assertTrue(usuarioAntes.isPresent(), "El usuario debería existir antes de eliminar");

        // Ejecución
        usuarioServicio.eliminarUsuario(id);
        Optional<Usuario> usuarioEliminado = usuarioRepositorio.findById(id);

        // Verificación
        assertTrue(usuarioEliminado.isEmpty(), "El usuario no debería existir después de eliminar");
    }

    @AfterEach
    void borrarUsuariosPrueba() {
        try {
            for (Usuario usuario : usuarios) {
                Integer id = usuario.getId();
                Optional<Usuario> usuarioBuscado = usuarioRepositorio.findById(id);

                if(usuarioBuscado != null) {
                    usuarioRepositorio.deleteById(id);
                }
            }
            usuarios.clear();

        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar los usuarios de prueba: " + e.getMessage());
        }
    }

}
