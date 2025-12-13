package com.example.biblioteca;

import com.example.biblioteca.Model.Prestamo;
import com.example.biblioteca.Model.RecursoBibliografico;
import com.example.biblioteca.Model.ResultadoPrestamo;
import com.example.biblioteca.Model.Usuario;
import com.example.biblioteca.Servicicos.PrestamoServicio;
import com.example.biblioteca.bibliotecaRepositorio.PrestamoRepositorio;
import com.example.biblioteca.bibliotecaRepositorio.RecursoRepositorio;
import com.example.biblioteca.bibliotecaRepositorio.UsuarioRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@ActiveProfiles("test")
class PrestamoServicioTest {

    @Autowired
    private PrestamoServicio prestamoServicio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private RecursoRepositorio recursoRepositorio;

    @Autowired
    private PrestamoRepositorio prestamoRepositorio;

    private Usuario usuario;
    private RecursoBibliografico recurso;

    @BeforeEach
    @Rollback
    void setUp() {

        //Crear usuario
        usuario = new Usuario();
        usuario.setId(11);
        usuario.setCedula(99999999L);
        usuario.setRol("usuario");
        usuario.setEmail("ana@test.com");
        usuario.setNombre("Ana");
        usuario.setApellido("López");
        usuario.setTelefono("3210000000");
        usuario.setPassword("claveSegura");
        usuario.setEstado(true);
        usuarioRepositorio.save(usuario);


        //Crear recurso disponible
        recurso = new RecursoBibliografico();
        recurso.setCodigoDeBarras("TEST-PRESTAMO-001");
        recurso.setTitulo("Libro Test Préstamo");
        recurso.setEstado("Disponible");
        recurso.setCategoria("general");
        recurso.setIdioma("ES");
        recurso.setLocalizacion("A1");
        recurso.setEstante("E1");
        recurso.setSignaturaTipografica("SIG-PRE-001");
        recurso.setColeccion("COL");
        recurso.setTipoDePublicacion("libro");
        recurso.setEstadoFisico("Bueno");
        recurso.setTema("Test");

        recursoRepositorio.save(recurso);
    }

    // ---------------------------------------------------
    // TESTS iniciarPrestamo()
    // ---------------------------------------------------

    @Test
    void testIniciarPrestamo_UsuarioNoExiste() {
        ResultadoPrestamo resultado =
                prestamoServicio.iniciarPrestamo(999999, recurso.getCodigoDeBarras());

        assertFalse(resultado.isExito());
        assertEquals("Usuario no encontrado", resultado.getMensaje());
    }

    @Test
    void testIniciarPrestamo_RecursoNoExiste() {
        ResultadoPrestamo resultado =
                prestamoServicio.iniciarPrestamo(usuario.getId(), "NO-EXISTE");

        assertFalse(resultado.isExito());
        assertEquals("Recurso bibliográfico no encontrado", resultado.getMensaje());
    }

    @Test
    void testIniciarPrestamo_RecursoNoDisponible() {
        recurso.setEstado("No disponible");
        recursoRepositorio.save(recurso);

        ResultadoPrestamo resultado =
                prestamoServicio.iniciarPrestamo(usuario.getId(), recurso.getCodigoDeBarras());

        assertFalse(resultado.isExito());
        assertTrue(resultado.getMensaje().contains("no se encuentra disponible"));
    }

    @Test
    void testIniciarPrestamo_Exitoso() {
        ResultadoPrestamo resultado =
                prestamoServicio.iniciarPrestamo(usuario.getId(), recurso.getCodigoDeBarras());

        assertTrue(resultado.isExito());
        assertEquals("Préstamo exitoso", resultado.getMensaje());

        Prestamo p = resultado.getPrestamo();
        assertNotNull(p);
        assertEquals(usuario.getId(), p.getUsuario().getId());
        assertEquals(recurso.getCodigoDeBarras(), p.getRecursoBibliografico().getCodigoDeBarras());
        assertTrue(p.getEstado());

        // Verifica que cambió estado del recurso
        RecursoBibliografico actualizado =
                recursoRepositorio.findById(recurso.getCodigoDeBarras()).get();

        assertEquals("No disponible", actualizado.getEstado());
    }


    // ---------------------------------------------------
    // TESTS finalizarPrestamo()
    // ---------------------------------------------------

    @Test
    void testFinalizarPrestamo_NoExiste() {
        ResultadoPrestamo resultado =
                prestamoServicio.finalizarPrestamo(999999);

        assertFalse(resultado.isExito());
        assertEquals("No se encontro el préstamo", resultado.getMensaje());
    }

    @Test
    void testFinalizarPrestamo_NoActivo() {
        RecursoBibliografico recursoBd =
            recursoRepositorio.findById(recurso.getCodigoDeBarras()).get();
        // Crear préstamo no activo
        Prestamo p = new Prestamo();
        p.setUsuario(usuario);
        p.setRecursoBibliografico(recursoBd);
        p.setEstado(false);
        p.setFechaAdquisicion(LocalDateTime.now().minusDays(10));
        p.setFechaDevolucion(LocalDateTime.now().minusDays(1));

        p = prestamoRepositorio.save(p);

        ResultadoPrestamo resultado =
                prestamoServicio.finalizarPrestamo(p.getId());

        assertFalse(resultado.isExito());
        assertEquals("El préstamo no está activo.", resultado.getMensaje());
    }

    @Test
    void testFinalizarPrestamo_Exitoso() {
        RecursoBibliografico recursoBd =
            recursoRepositorio.findById(recurso.getCodigoDeBarras()).get();
        // Crear préstamo activo
        Prestamo p = new Prestamo();
        p.setUsuario(usuario);
        p.setRecursoBibliografico(recursoBd);
        p.setEstado(true);
        p.setFechaAdquisicion(LocalDateTime.now().minusDays(5));
        p.setFechaDevolucion(LocalDateTime.now().plusDays(5));

        p = prestamoRepositorio.save(p);

        recursoBd.setPrestamo(p);
        recursoBd.setEstado("No disponible");
        recursoRepositorio.save(recursoBd);

        ResultadoPrestamo resultado =
                prestamoServicio.finalizarPrestamo(p.getId());

        assertTrue(resultado.isExito());
        assertEquals("El préstamo finalizó", resultado.getMensaje());

        Prestamo actualizado =
                prestamoRepositorio.findById(p.getId()).get();

        assertFalse(actualizado.getEstado());
        assertNotNull(actualizado.getFechaDevolucionReal());

        RecursoBibliografico rbActualizado =
                recursoRepositorio.findById(recurso.getCodigoDeBarras()).get();

        assertEquals("Disponible", rbActualizado.getEstado());
    }


    // ---------------------------------------------------
    // TESTS buscar / borrar
    // ---------------------------------------------------

    @Test
    void testBuscarPrestamoPorId() {
        ResultadoPrestamo res =
                prestamoServicio.iniciarPrestamo(usuario.getId(), recurso.getCodigoDeBarras());

        Prestamo p = res.getPrestamo();

        var encontrado = prestamoServicio.buscarPrestamoPorId(p.getId());

        assertTrue(encontrado.isPresent());
        assertEquals(p.getId(), encontrado.get().getId());
    }

    @Test
    void testBorrarPrestamo() {
        ResultadoPrestamo res =
                prestamoServicio.iniciarPrestamo(usuario.getId(), recurso.getCodigoDeBarras());

        Prestamo p = res.getPrestamo();

        var eliminado = prestamoServicio.borrarPrestamo(p.getId());

        assertTrue(eliminado.isPresent());
        assertTrue(
                prestamoRepositorio.findById(p.getId()).isEmpty()
        );
        assertEquals(p.getId(), eliminado.get().getId());
    }
}