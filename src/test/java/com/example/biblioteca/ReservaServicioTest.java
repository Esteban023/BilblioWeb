package com.example.biblioteca;

import com.example.biblioteca.DTO.ReservaDTO;
import com.example.biblioteca.Model.Prestamo;
import com.example.biblioteca.Model.RecursoBibliografico;
import com.example.biblioteca.Model.Reserva;
import com.example.biblioteca.Model.Usuario;
import com.example.biblioteca.Servicicos.ReservaServicio;
import com.example.biblioteca.bibliotecaRepositorio.PrestamoRepositorio;
import com.example.biblioteca.bibliotecaRepositorio.RecursoRepositorio;
import com.example.biblioteca.bibliotecaRepositorio.ReservaRepositorio;
import com.example.biblioteca.bibliotecaRepositorio.UsuarioRepositorio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@ActiveProfiles("test")
public class ReservaServicioTest {

    @Autowired
    private ReservaServicio reservaServicio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private RecursoRepositorio recursoRepositorio;

    @Autowired
    private PrestamoRepositorio prestamoRepositorio;

    @Autowired
    private ReservaRepositorio reservaRepositorio;

    private Usuario usuario;
    private RecursoBibliografico recurso;

    @BeforeEach
    @Rollback
    void setUp() {

        // Crear usuario
        usuario = new Usuario();
        usuario.setId(11);
        usuario.setCedula(99999999L);
        usuario.setRol("usuario");
        usuario.setEmail("Perez@gmail.com");
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setTelefono("3210000000");
        usuario.setPassword("claveSegura");
        usuario.setEstado(true);
        usuarioRepositorio.save(usuario);

        // Crear recurso NO disponible (para permitir reservas)
        recurso = new RecursoBibliografico();
        recurso.setCodigoDeBarras("TEST-12345");
        recurso.setTitulo("Libro de Prueba");
        recurso.setEstado("No disponible");
        recurso.setCategoria("general");
        recurso.setIdioma("ES");
        recurso.setLocalizacion("A1");
        recurso.setEstante("E1");
        recurso.setSignaturaTipografica("SIG-999");
        recurso.setColeccion("COL");
        recurso.setTipoDePublicacion("libro");
        recurso.setEstadoFisico("Bueno");
        recurso.setTema("Test");

        // Crear préstamo activo para poder calcular vencimientos
        Prestamo prestamo = new Prestamo();
        prestamo.setEstado(true);
        prestamo.setUsuario(usuario);
        prestamo.setRecursoBibliografico(recurso);
        prestamo.setFechaAdquisicion(LocalDateTime.now().minusDays(5));
        prestamo.setFechaDevolucion(LocalDateTime.now().plusDays(5));
        prestamo = prestamoRepositorio.save(prestamo);

        recurso.setPrestamo(prestamo);
        recursoRepositorio.save(recurso);

        Reserva reserva = crearReservaValida(usuario, recurso);
    }

    private Reserva crearReservaValida(Usuario usuario, RecursoBibliografico recurso) {
        Reserva reserva = new Reserva();
        reserva.setCodigoReserva();
        reserva.setUsuario(usuario);
        reserva.setRecursoBibliografico(recurso);
        reserva.setFechaReserva(LocalDateTime.now());
        reserva.setFechaLimiteRetiro(LocalDateTime.now().plusDays(3));
        reserva.setEstado(Reserva.EstadoReserva.ACTIVA);
        reserva.setPosicionCola(1);
        return reserva;
    }


    // ---------------------------------------------------
    //   TESTS PARA crearReserva
    // ---------------------------------------------------

    @Test
    void testCrearReserva_UsuarioNoExiste() {
        ReservaDTO dto = reservaServicio.crearReserva(99999999, recurso.getCodigoDeBarras());
        assertFalse(dto.isExito());
        assertEquals("Usuario no encontrado", dto.getMensaje());
    }

    @Test
    void testCrearReserva_RecursoNoExiste() {
        ReservaDTO dto = reservaServicio.crearReserva(usuario.getId(), "NO-EXISTE");
        assertFalse(dto.isExito());
        assertEquals("Recurso bibliográfico no encontrado", dto.getMensaje());
    }

    @Test
    void testCrearReserva_RecursoDisponibleDebeFallar() {
        recurso.getPrestamo().setEstado(false);
        prestamoRepositorio.save(recurso.getPrestamo());
        recurso.setPrestamo(null);
        recursoRepositorio.save(recurso);
        
        recurso.setEstado("Disponible"); // disponible → produce error
        recursoRepositorio.save(recurso);

        ReservaDTO dto = reservaServicio.crearReserva(usuario.getId(), recurso.getCodigoDeBarras());
        assertFalse(dto.isExito());
        assertEquals("El recurso '"+ recurso.getTitulo() + "' se encuentra disponible. Use la función de préstamo"
        , dto.getMensaje());
    }

    @Test
    void testCrearReserva_ReservaActivaYaExiste() {
        Reserva reserva = crearReservaValida(usuario, recurso);
        reservaRepositorio.save(reserva);

        ReservaDTO dto = reservaServicio.crearReserva(usuario.getId(), recurso.getCodigoDeBarras());
        assertFalse(dto.isExito());
        assertEquals("El usuario ya tiene una reserva activa para el recurso '"+ recurso.getTitulo() +"'"
        , dto.getMensaje());
    }

    @Test
    void testCrearReserva_PrestamoActivoYaExiste() {
        // ya se creó un préstamo activo en el setup
        ReservaDTO dto = reservaServicio.crearReserva(usuario.getId(), recurso.getCodigoDeBarras());
        assertFalse(dto.isExito());
        assertEquals("El usuario ya tiene un préstamo activo para el recurso '"+ recurso.getTitulo() +"'"
        , dto.getMensaje());
    }

    @Test
    void testCrearReserva_ReservaCreadaCorrectamente() {
        // desactivar el préstamo para permitir crear la reserva
        recurso.getPrestamo().setEstado(false);
        prestamoRepositorio.save(recurso.getPrestamo());

        ReservaDTO dto = reservaServicio.crearReserva(usuario.getId(), recurso.getCodigoDeBarras());
        assertTrue(dto.isExito());
        assertEquals("Se reservó con éxito el recurso '" + recurso.getTitulo() + "'"
        , dto.getMensaje());

        Reserva r = dto.getReserva();
        assertNotNull(r.getCodigoReserva());
        assertEquals(usuario.getId(), r.getUsuario().getId());
        assertEquals(recurso.getCodigoDeBarras(), r.getRecursoBibliografico().getCodigoDeBarras());
    }


    // ---------------------------------------------------
    //   TESTS PARA cancelarReserva
    // ---------------------------------------------------

    @Test
    void testCancelarReserva_NoExiste() {       
        ResponseEntity<ReservaDTO> r = reservaServicio.cancelarReserva("XXXXXXXX");
        assertFalse(r.getBody().isExito());
        assertEquals("Reserva no encontrada", r.getBody().getMensaje());
    }

    @Test
    void testCancelarReserva_NoActiva() {
        Reserva reserva = crearReservaValida(usuario, recurso);
        reserva.setEstado(Reserva.EstadoReserva.CANCELADA);
        reservaRepositorio.save(reserva);

        ResponseEntity<ReservaDTO> r = reservaServicio.cancelarReserva(reserva.getCodigoReserva());
        assertFalse(r.getBody().isExito());
        assertEquals("La reserva no está activa", r.getBody().getMensaje());
    }

    @Test
    void testCancelarReserva_Exitosa() {
        Reserva reserva = crearReservaValida(usuario, recurso);
        reservaRepositorio.save(reserva);

        ResponseEntity<ReservaDTO> r = reservaServicio.cancelarReserva(reserva.getCodigoReserva());
        assertTrue(r.getBody().isExito());
        assertEquals("Reserva cancelada exitosamente", r.getBody().getMensaje());
    }
}