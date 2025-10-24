package com.example.biblioteca.Servicicos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.example.biblioteca.Model.Prestamo;
import com.example.biblioteca.Model.RecursoBibliografico;
import com.example.biblioteca.Model.ResultadoPrestamo;
import com.example.biblioteca.Model.Usuario;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.biblioteca.bibliotecaRepositorio.PrestamoRepositorio;
import com.example.biblioteca.bibliotecaRepositorio.RecursoRepositorio;
import com.example.biblioteca.bibliotecaRepositorio.UsuarioRepositorio;

@Service
public class PrestamoServicio {
    @Autowired
    PrestamoRepositorio prestamoRepositorio;

    @Autowired 
    UsuarioRepositorio usuarioRepositorio;

    private static final int DIAS_PRESTAMO_GENERAL = 20;
    private static final int DIAS_PRESTAMO_RESERVA = 2;

    @Autowired
    RecursoRepositorio rbRepositorio;

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

        boolean isPresent = userOpt.isPresent() && rbOpt.isPresent();
        if(!isPresent) {
            return new ResultadoPrestamo(false, "Usuario o recurso no encontrado");
        }

        Usuario usuario = userOpt.get();
        LocalDateTime fechaInicio = LocalDateTime.now();
        RecursoBibliografico rb = rbOpt.get();

        boolean disponible = rb.getEstado().equals("Disponible");
        if(!disponible){
            return new ResultadoPrestamo(
                false, "El recurso con codigo " + 
                rb.getCodigoDeBarras() + 
                " no se encuentra disponible"
            );
        }

        Prestamo prestamo = new Prestamo();
        LocalDateTime fechaDevolucion = calcularFechaDevolucion(rb, fechaInicio);
        prestamo.setUsuario(usuario);
        prestamo.setEstado(true);
        prestamo.setRecursoBibliografico(rb);
        prestamo.setFechaAdquisicion(fechaInicio);
        prestamo.setFechaDevolucion(fechaDevolucion);

        //Cambiar el estado del libro
        rb.setEstado("No Disponible");
        rbRepositorio.save(rb);

        //Guardar el prestamo en el usuario
        Prestamo prestamoGuardado = prestamoRepositorio.save(prestamo);
        usuario.getPrestamos().add(prestamo);
        usuarioRepositorio.save(usuario);

        return new ResultadoPrestamo(true, "Prestamo exitoso", prestamoGuardado);
    }

    public ResultadoPrestamo finalizarPrestamo(Integer idPrestamo) {
        Optional<Prestamo> prestamoOpt = prestamoRepositorio.findById(idPrestamo);

        boolean isPresent = prestamoOpt.isPresent();
        if (!isPresent) {
            return new ResultadoPrestamo(false, "No se encontro el prestamo");
        }

        Prestamo prestamo = prestamoOpt.get();
        boolean estado = prestamo.getEstado();
        if (!estado) {
            return new ResultadoPrestamo(false, "El prestamo ");
        }

        RecursoBibliografico rb = prestamo.getRecursoBibliografico();

        // 1️⃣ Cambiar estados
        prestamo.setEstado(false);
        rb.setEstado("Disponible");

        // 2️⃣ Registrar fecha real de devolución
        LocalDateTime fechaDevolucionReal = LocalDateTime.now();
        prestamo.setFechaDevolucionReal(fechaDevolucionReal);

        // 3️⃣ Guardar cambios
        rbRepositorio.save(rb);
        Prestamo prestamoFinalizado = prestamoRepositorio.save(prestamo);

        return new ResultadoPrestamo(estado, "El prestamo finalizo", prestamoFinalizado);
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
}
