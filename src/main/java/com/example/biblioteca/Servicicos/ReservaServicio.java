package com.example.biblioteca.Servicicos;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.example.biblioteca.Model.Reserva;
import com.example.biblioteca.Model.Usuario;
import com.example.biblioteca.DTO.ReservaDTO;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;

import com.example.biblioteca.Model.RecursoBibliografico;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.biblioteca.Model.Utilidades.Email;
import com.example.biblioteca.Model.Utilidades.FechaComparador;
import com.example.biblioteca.bibliotecaRepositorio.RecursoRepositorio;
import com.example.biblioteca.bibliotecaRepositorio.ReservaRepositorio;
import com.example.biblioteca.bibliotecaRepositorio.UsuarioRepositorio;
import com.example.biblioteca.bibliotecaRepositorio.PrestamoRepositorio;

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

    @Autowired
    EmailServicios emailServicios;

    @Autowired
    private TemplateEngine templateEngine;

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
        var usuarioOpt = usuarioRepositorio.findById(idUsuario);
        if (usuarioOpt.isEmpty()) {
            return new ReservaDTO("Usuario no encontrado", false);
        }
        var recursoOpt = recursoRepositorio.findById(codigoBarras);
        String tituloRecurso;
        if (recursoOpt.isEmpty()) {
            return new ReservaDTO("Recurso bibliográfico no encontrado", false);
        }else {
            tituloRecurso = recursoOpt.get().getTitulo();
        }

        // --- Validaciones de existencia de reserva o préstamo ---
        if (reservaRepositorio.findReservasActivasPorUsuarioYRecurso(
                codigoBarras, Reserva.EstadoReserva.ACTIVA, idUsuario).isPresent()) {
            return new ReservaDTO("El usuario ya tiene una reserva activa para el recurso '"+ tituloRecurso +"'"
            , false);
        }

        if (prestamoRepositorio.buscarPrestamoActivoPorCodigoDeBarras(
                codigoBarras, idUsuario, true).isPresent()) {
            return new ReservaDTO("El usuario ya tiene un préstamo activo para el recurso '"+ tituloRecurso +"'"
            , false);
        }

        Usuario usuario = usuarioOpt.get();
        RecursoBibliografico recurso = recursoOpt.get();

        // --- Verificar disponibilidad ---
        if (!"No disponible".equals(recurso.getEstado())) {
            return new ReservaDTO("El recurso '"+ tituloRecurso + "' se encuentra disponible. Use la función de préstamo", false);
        }

        // --- Crear reserva ---
        int nuevaPosicion = reservaRepositorio.ultimoPuestoFila(codigoBarras) + 1;
        LocalDateTime fechaLimite = calcularFechaVencimiento(recurso, nuevaPosicion);

        Reserva reserva = crearReservaEntity(usuario, recurso, nuevaPosicion, fechaLimite);
        Reserva reservaGuardada = reservaRepositorio.save(reserva);

        actualizarRelaciones(usuario, recurso, reservaGuardada);

        return new ReservaDTO("Se reservó con éxito el recurso '" + reservaGuardada.getRecursoBibliografico().getTitulo() + "'"
        , true, reservaGuardada);
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

    public List<Reserva> getPorUsuario (Integer idUsuario) {
        return reservaRepositorio.findByUsuarioId(idUsuario);
    }

    
    private String generarBodyMail(Reserva reserva, DateTimeFormatter formatter){
        String mensaje = String.format(
            "Estimado %s,\n\n" +
            "El recurso “%s” se reservó satisfactoriamente para el %s. " +
            "Será notificado cuando el recurso esté listo para ser recogido.\n\n" +
            "Saludos cordiales.",
            reserva.getUsuario().getNombre().concat(" ").concat(reserva.getUsuario().getApellido()),
            reserva.getRecursoBibliografico().getTitulo(),
            reserva.getFechaReserva().format(formatter)
            
        );
        
        return mensaje;
    }
    private String generarBodyMailVarios(List<Reserva> reservas, Usuario user){
        Context context = new Context();
        context.setVariable("reservas", reservas);
        context.setVariable("usuario", user);
        String bodyMail = templateEngine.process("tablaMail", context);
        return bodyMail;
    }
    @Async
    public void enviarCorreoReserva(Reserva reserva, DateTimeFormatter formatter){
        String bodyMail = generarBodyMail(reserva, formatter);
        Email email = new Email(
            reserva.getUsuario().getEmail(),
            bodyMail,
            "Recurso bibliografico reservado.",
            null,
            false
        );
        emailServicios.enviarCorreo(email);
    }
    @Async
    public void enviarCorreoVariasReservas(List<Reserva> reservas, Usuario user){
        String bodyMail = generarBodyMailVarios(reservas, user);
        Email email = new Email(
            user.getEmail(),
            bodyMail,
            "Reserva de varios recursos",
            null,
            true
        );
        emailServicios.enviarCorreo(email);
    }
}
