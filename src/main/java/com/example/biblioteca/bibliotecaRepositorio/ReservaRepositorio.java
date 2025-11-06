package com.example.biblioteca.bibliotecaRepositorio;


import java.util.Optional;
import com.example.biblioteca.Model.Reserva;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepositorio extends JpaRepository<Reserva, String> {
    @Query(value = "SELECT COALESCE(MAX(r.posicion_cola), 0) FROM reserva r " + 
       "JOIN recurso_bibliografico rb ON r.codigo_de_barras = rb.codigo_de_barras " +
       "WHERE rb.codigo_de_barras = ?1", nativeQuery = true)
    Integer ultimoPuestoFila(String codigoDeBarras);

    @Query("""
            SELECT r FROM Reserva r 
            WHERE r.recursoBibliografico.codigoDeBarras = :codigoDeBarras 
                AND r.estado = :estadoReserva
                AND r.usuario.id = :usuarioId
    """)
    Optional<Reserva> findReservasActivasPorUsuarioYRecurso(
        @Param("codigoDeBarras") String codigoDeBarras,
        @Param("estadoReserva") Reserva.EstadoReserva estadoReserva,
        @Param("usuarioId") Integer usuarioId);

}
