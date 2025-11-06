package com.example.biblioteca.Servicicos;
import java.time.LocalDateTime;
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

    private static final int DIAS_PRESTAMO_GENERAL = 15;
    private static final int DIAS_PRESTAMO_RESERVA = 2;

    @Autowired
    RecursoRepositorio rbRepositorio;

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
                false, "El recurso con codigo " + 
                rb.getCodigoDeBarras() + 
                " no se encuentra disponible"
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

        return new ResultadoPrestamo(true, "Préstamo creado correctamente", prestamoGuardado);
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
}
