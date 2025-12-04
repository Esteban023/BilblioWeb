package com.example.biblioteca.Servicicos;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.example.biblioteca.Model.Prestamo;
import com.example.biblioteca.Model.RecursoBibliografico;
import com.example.biblioteca.Model.Reserva;
import com.example.biblioteca.Model.ResultadoPrestamo;
import com.example.biblioteca.Model.Usuario;
import com.example.biblioteca.Model.Utilidades.Email;
import com.example.biblioteca.bibliotecaRepositorio.PrestamoRepositorio;
import com.example.biblioteca.bibliotecaRepositorio.RecursoRepositorio;
import com.example.biblioteca.bibliotecaRepositorio.ReservaRepositorio;
import com.example.biblioteca.bibliotecaRepositorio.UsuarioRepositorio;

@Service
public class PrestamoServicio {
    @Autowired
    PrestamoRepositorio prestamoRepositorio;

    @Autowired 
    UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private EmailServicios emailServicios;
    
    @Autowired
    private TemplateEngine templateEngine;

    private static final int DIAS_PRESTAMO_GENERAL = 15;
    private static final int DIAS_PRESTAMO_RESERVA = 2;

    @Autowired
    RecursoRepositorio rbRepositorio;

    @Autowired
    ReservaRepositorio reservaRepositorio;

    private Prestamo crearPrestamoEntity(Usuario usuario, RecursoBibliografico rb, LocalDateTime fechaDevolucion, LocalDateTime fechaAdquisicion) {
        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuario);
        prestamo.setRecursoBibliografico(rb);
        prestamo.setFechaDevolucion(fechaDevolucion);
        prestamo.setFechaAdquisicion(fechaAdquisicion);
        prestamo.setEstado(true);
        return prestamo;
    }

    private LocalDateTime calcularFechaDevolucion(RecursoBibliografico rb, LocalDateTime date) {
        String categoria = rb.getCategoria().toLowerCase().trim();

        switch (categoria) {
            case "reserva":
                return date.plusDays(DIAS_PRESTAMO_RESERVA);
        
            case "general":
                return date.plusDays(DIAS_PRESTAMO_GENERAL);

            default: throw new IllegalArgumentException(
                "Categoría no válida: " + categoria + 
                ". Las categorías permitidas son: 'general' y 'reserva'"
            );
        }

    }

    public ResultadoPrestamo iniciarPrestamo(Integer idUsuario, String codigoBarras) {
        Optional<Usuario> userOpt = usuarioRepositorio.findById(idUsuario);
        Optional<RecursoBibliografico> rbOpt = rbRepositorio.findById(codigoBarras);

        boolean usuarioExiste = userOpt.isPresent(); 
        if(!usuarioExiste) {
            return new ResultadoPrestamo(false, "Usuario no encontrado");
        }

        boolean recursoExiste = rbOpt.isPresent();
        if (!recursoExiste) {
            return new ResultadoPrestamo(false, "Recurso bibliográfico no encontrado");        
        }

        Usuario usuario = userOpt.get();
        LocalDateTime fechaInicio = LocalDateTime.now();
        RecursoBibliografico rb = rbOpt.get();

        boolean disponible = rb.getEstado().equals("Disponible");
        if(!disponible){
            return new ResultadoPrestamo(
                false, "El recurso '" + 
                rb.getTitulo() + 
                "' no se encuentra disponible"
            );
        }

        LocalDateTime fechaDevolucion = calcularFechaDevolucion(rb, fechaInicio); 
        // 1. Crear la entidad Prestamo
        Prestamo prestamo = crearPrestamoEntity(usuario, rb, fechaDevolucion, fechaInicio);
        
        // 2. ESTABLECER RELACIÓN BIDIRECCIONAL ANTES DE GUARDAR
        prestamo.setRecursoBibliografico(rb);  // Del préstamo al recurso
        rb.setPrestamo(prestamo);              // Del recurso al préstamo
        rb.setEstado("No disponible");
        
        // 3. Ahora guardar el préstamo (con la relación ya establecida)

        Prestamo prestamoGuardado = prestamoRepositorio.save(prestamo);
        
        // 4. Actualizar el usuario
        usuario.getPrestamos().add(prestamoGuardado);
        usuarioRepositorio.save(usuario);
        
        // 5. El recurso bibliográfico YA debería estar actualizado por la relación
        // pero por si acaso lo guardamos también
        rbRepositorio.save(rb);

        return new ResultadoPrestamo(true, "Préstamo exitoso", prestamoGuardado);
    }

    public ResultadoPrestamo finalizarPrestamo(Integer idPrestamo) {
        Optional<Prestamo> prestamoOpt = prestamoRepositorio.findById(idPrestamo);

        boolean isPresent = prestamoOpt.isPresent();
        if (!isPresent) {
            return new ResultadoPrestamo(false, "No se encontro el préstamo");
        }

        Prestamo prestamo = prestamoOpt.get();
        boolean estado = prestamo.getEstado();
        if (!estado) {
            return new ResultadoPrestamo(false, "El préstamo no está activo.");
        }

        RecursoBibliografico rb = prestamo.getRecursoBibliografico();

        // 1. Cambiar estados
        prestamo.setEstado(false);
        rb.setEstado("Disponible");

        // 2. egistrar fecha real de devolución
        LocalDateTime fechaDevolucionReal = LocalDateTime.now();
        prestamo.setFechaDevolucionReal(fechaDevolucionReal);

        // 3. Guardar cambios
        rbRepositorio.save(rb);
        Prestamo prestamoFinalizado = prestamoRepositorio.save(prestamo);

        return new ResultadoPrestamo(estado, "El préstamo finalizó", prestamoFinalizado);
    }

    public ResultadoPrestamo enviarCorreo(Integer idPrestamo){
        Optional<Prestamo> prestamoOpt = prestamoRepositorio.findById(idPrestamo);

        boolean isPresent = prestamoOpt.isPresent();
        if (!isPresent) {
            return new ResultadoPrestamo(false, "No se encontró el préstamo");
        }

        Prestamo prestamo = prestamoOpt.get();
        RecursoBibliografico rb = prestamo.getRecursoBibliografico();

        Optional<Usuario> userOpt = usuarioRepositorio.findPorReservaCodDeBarras(rb.getCodigoDeBarras());
        boolean usuarioExiste = userOpt.isPresent(); 
        if(!usuarioExiste) {
            return new ResultadoPrestamo(false, "Usuario no encontrado: " + userOpt + rb.getCodigoDeBarras());
        }

        Usuario usuario = userOpt.get();

        Optional<Reserva> reservaOpt = reservaRepositorio.findReservasActivasPorUsuarioYRecurso(rb.getCodigoDeBarras(), Reserva.EstadoReserva.ACTIVA, usuario.getId());
        boolean reservaExiste = reservaOpt.isPresent(); 
        if(!reservaExiste) {
            return new ResultadoPrestamo(false, "No hay reservas para este recurso.");
        }

        Reserva reserva = reservaOpt.get();

        LocalDateTime fechaInicio = LocalDateTime.now();
        LocalDateTime fechaDevolucion = calcularFechaDevolucion(rb, fechaInicio);

        Email detalles = new Email();        
        detalles.setRecipiente(usuario.getEmail());
        detalles.setAsunto("Libro '" + rb.getTitulo() + "' listo para reclamar");
        detalles.setMsgBody("Hola " + usuario.getNombre() + ", el libro '" + rb.getTitulo() + 
            "' que reservaste el " + reserva.getFechaReserva().toString().substring(0,10) 
            + " ya está disponible para su entrega.\n\nDebe devolver el libro a más tardar en la siguiente fecha: " 
            + fechaDevolucion.toString().substring(0,10) + ".\n\nTiene 24 horas para recoger el libro solicitado.");

        try {
            emailServicios.enviarCorreo(detalles);
            return new ResultadoPrestamo(true, "Se notificó al primer reservado en cola sobre la disponibilidad del libro.");
        } catch (Exception e) {
            return new ResultadoPrestamo(false, "" + e);
        }
    }

    public Optional<Prestamo> buscarPrestamoPorId(Integer id) {
        return prestamoRepositorio.findById(id);
    }

    public Optional<Prestamo> borrarPrestamo(Integer id) {
        Optional<Prestamo> prestamo = prestamoRepositorio.findById(id);

        boolean isPresent = prestamo.isPresent();
        if(isPresent) {
            Prestamo prestamoEncontrado = prestamo.get();
            prestamoRepositorio.delete(prestamoEncontrado);
            return Optional.of(prestamoEncontrado);
        } else {
            return Optional.empty();
        }
    }
    public List<Prestamo> getPrestamosPorUsuario(Integer usuarioId){
        return prestamoRepositorio.findByUsuarioId(usuarioId);
    }
    
    public String generarBodyMail(Prestamo prestamo, DateTimeFormatter formatter) {
        String cuerpo = String.format(
            "Hola %s, tu préstamo del recurso “%s” se ha realizado exitosamente.  \r\nPor favor reclámarlo en la sede correspondiente dentro de las próximas 24 horas.  \r\nRecuerda que debe devolverlo el día %s. Si no lo reclamas en el plazo indicado, el prestamo será cancelado.  \r\nGracias por utilizar nuestros servicios.",
            prestamo.getUsuario().getNombre().concat(" ").concat(prestamo.getUsuario().getApellido()),
            prestamo.getRecursoBibliografico().getTitulo(),
            prestamo.getFechaDevolucion().format(formatter)
            
        );
        return cuerpo;

    }

    public String buildMensajePrestamo(Prestamo prestamo, DateTimeFormatter formatter) {
        return String.format(
                "El recurso con código (%s) se prestó satisfactoriamente el %s. "
                        + "Por favor, devuélvalo el %s o antes.",
                prestamo.getRecursoBibliografico().getTitulo(),
                prestamo.getRecursoBibliografico().getCodigoDeBarras(),
                prestamo.getFechaAdquisicion().format(formatter),
                prestamo.getFechaDevolucion().format(formatter));
    }

    public void enviarCorreoPrestamo(Prestamo prestamo, DateTimeFormatter formatter) {
        String bodyMail = generarBodyMail(prestamo, formatter);
        Email email = new Email(
                prestamo.getUsuario().getEmail(),
                bodyMail,
                "Préstamo de recurso Bibliográfico",
                null, 
            false
        );
        emailServicios.enviarCorreo(email);
    }
    public String generarBodyMailVarios(List<Prestamo> prestamos, Usuario user){
        Context context = new Context();
        context.setVariable("usuario", user);
        context.setVariable("prestamos", prestamos);

        //Se procesa el template con las variables
        String bodyMail = templateEngine.process("tablaMail", context);
        return bodyMail;

    }
    public void enviarCorreoVariosPrestamos(List<Prestamo> prestamos, Usuario user){
        String bodyMail = generarBodyMailVarios(prestamos, user);
        Email email = new Email(
            user.getEmail(),
            bodyMail,
            "Prestamo de varios recurso",
            null,
            true
        );
        emailServicios.enviarCorreo(email);
    }
}
