package com.example.biblioteca.Servicicos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.time.LocalDateTime;
import com.example.biblioteca.Model.Utilidades.FechaComparador;
import com.example.biblioteca.Model.Reserva;
import com.example.biblioteca.Model.Reserva.EstadoReserva;
import com.example.biblioteca.Model.Usuario;
import com.example.biblioteca.DTO.ReservaDTO;
import org.springframework.stereotype.Service;

import com.example.biblioteca.Model.Prestamo;
import com.example.biblioteca.Model.RecursoBibliografico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.example.biblioteca.bibliotecaRepositorio.PrestamoRepositorio;
import com.example.biblioteca.bibliotecaRepositorio.RecursoRepositorio;
import com.example.biblioteca.bibliotecaRepositorio.ReservaRepositorio;
import com.example.biblioteca.bibliotecaRepositorio.UsuarioRepositorio;


@Service
public class ReservaServicio {
    @Autowired
    ReservaRepositorio reservaRepositorio;

    @Autowired
    UsuarioRepositorio usuarioRepositorio;

    @Autowired
    RecursoRepositorio recursoRepositorio;

    @Autowired
    PrestamoRepositorio prestamoRepositorio;

    private LocalDateTime calcularFechaVencimiento(RecursoBibliografico rb, Integer puestoFila) {
        String categoria = rb.getCategoria().toLowerCase().trim();
        LocalDateTime fechaBase = rb.getPrestamo().getFechaDevolucion();

        if (puestoFila == 1) {
            return fechaBase;
        }
        
        int diasPorPuesto = "reserva".equals(categoria) ? 2 : 15;
        int diasAdicionales = puestoFila * diasPorPuesto;
        
        return fechaBase.plusDays(diasAdicionales);
    }

    public ReservaDTO crearReserva(Integer idUsuario, String codigoBarras) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findById(idUsuario);
        Optional<RecursoBibliografico> recursoOpt = recursoRepositorio.findById(codigoBarras);

        Optional<Reserva> reservaExistenteOpt = reservaRepositorio.findReservasActivasPorUsuarioYRecurso(
            codigoBarras, 
            Reserva.EstadoReserva.ACTIVA, 
            idUsuario
        );

        Optional<Prestamo> prestamoOpt = prestamoRepositorio.buscarPrestamoActivoPorCodigoDeBarras(
            codigoBarras, 
            idUsuario, 
            true
        );
        
        boolean usuarioExiste = usuarioOpt.isPresent();
        if (!usuarioExiste) {
            return new ReservaDTO("Usuario no encontrado", false);
        }

        boolean recursoExiste = recursoOpt.isPresent();
        if (!recursoExiste) {
            return new ReservaDTO("Recurso bibliográfico no encontrado", false);
        }

        boolean reservaExistente = reservaExistenteOpt.isPresent();
        if (reservaExistente) {
            return new ReservaDTO("El usuario ya tiene una reserva activa para este recurso", false);
        }

        boolean prestamoExistente = prestamoOpt.isPresent();
        if (prestamoExistente) {
            return new ReservaDTO("El usuario ya tiene un préstamo activo para este recurso", false);
        }
        
        Usuario usuario = usuarioOpt.get();
        RecursoBibliografico recurso = recursoOpt.get();

        String estadoRecurso = recurso.getEstado();
        boolean recursoNoDisponible = "No disponible".equals(estadoRecurso);
        if (!recursoNoDisponible) {
            return new ReservaDTO("El recurso se encuentra disponible. Use la función de préstamo", false);
        }
        
        Integer nuevaPosicion = reservaRepositorio.ultimoPuestoFila(codigoBarras) + 1;
        LocalDateTime fechaLimite = calcularFechaVencimiento(recurso, nuevaPosicion);
        
        Reserva reserva = crearReservaEntity(usuario, recurso, nuevaPosicion, fechaLimite);
        
        Reserva reservaGuardada = reservaRepositorio.save(reserva);
        actualizarRelaciones(usuario, recurso, reservaGuardada);
        
        return new ReservaDTO("Se reservó con éxito el recurso", true, reservaGuardada);
    }

    private Reserva crearReservaEntity(Usuario usuario, RecursoBibliografico recurso, Integer posicion, LocalDateTime fechaLimite) {
        Reserva reserva = new Reserva();
        reserva.setCodigoReserva();
        reserva.setUsuario(usuario);
        reserva.setRecursoBibliografico(recurso);
        reserva.setFechaReserva(LocalDateTime.now());
        reserva.setPosicionCola(posicion);
        reserva.setFechaLimiteRetiro(fechaLimite);
        reserva.setEstado(Reserva.EstadoReserva.ACTIVA);
        return reserva;
    }

    private void actualizarRelaciones(Usuario usuario, RecursoBibliografico recurso, Reserva reserva) {
        if (usuario.getReservas() == null) {
            usuario.setReservas(new ArrayList<>());
        }
        usuario.getReservas().add(reserva);

        if (recurso.getReserva() == null) {
            recurso.setReserva(new ArrayList<>());
        }
        recurso.getReserva().add(reserva);
        
        recursoRepositorio.save(recurso);
        usuarioRepositorio.save(usuario);
    }

    private void actualizarPosicionesCola(RecursoBibliografico recurso) {
        List<Reserva> reservasActivas = reservaRepositorio.findByRecursoBibliograficoAndEstado(
            recurso, 
            Reserva.EstadoReserva.ACTIVA
        );

        FechaComparador fechaComparador = new FechaComparador();
        reservasActivas.sort(fechaComparador);

        int posicion = 1;
        for (Reserva reserva : reservasActivas) {
            reserva.setPosicionCola(posicion);
            posicion++;
        }

        reservaRepositorio.saveAll(reservasActivas);
    }

    public ResponseEntity<ReservaDTO> cancelarReserva(String idReserva) {
        // Buscar la reserva
        Optional<Reserva> reservaOpt = reservaRepositorio.findById(idReserva);
        
        if (reservaOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(
                    new ReservaDTO("Reserva no encontrada", false)
                );
        }
        
        Reserva reserva = reservaOpt.get();
        
        // Verificar que esté activa
        if (reserva.getEstado() != Reserva.EstadoReserva.ACTIVA) {
            return ResponseEntity.badRequest()
                .body(
                    new ReservaDTO("La reserva no está activa", false)
                );
        }
        
        // Guardar referencia al recurso antes de cancelar
        RecursoBibliografico recurso = reserva.getRecursoBibliografico();
        
        // Cambiar estado a cancelada
        reserva.setEstado(Reserva.EstadoReserva.CANCELADA);
        reserva.setPosicionCola(null); // Opcional: limpiar la posición
        reservaRepositorio.save(reserva);
        
        // Actualizar posiciones de las reservas restantes
        actualizarPosicionesCola(recurso);
        
        return ResponseEntity.ok()
            .body(
                new ReservaDTO("Reserva cancelada exitosamente", true)
            );
    }

}
