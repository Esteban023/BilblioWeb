package com.example.biblioteca.Controlador;

import com.example.biblioteca.DTO.ReservaDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.biblioteca.Servicicos.ReservaServicio;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/reservas")

public class ReservaControlador {
    @Autowired
    ReservaServicio reservaServicio;

    @PostMapping
    public ResponseEntity<ReservaDTO> cargarReserva(@RequestBody ReservaDTO request) {
        Integer id = request.getIdUsuario();
        String codigo = request.getCodigoDeBarras();

        if (id == null) {
            ReservaDTO badResponse = new ReservaDTO("El id es nulo", false);
            return ResponseEntity.badRequest().body(badResponse);
        }
        
        if(codigo == null) {
            ReservaDTO badResponse = new ReservaDTO("El codigo es nulo", false);
            return ResponseEntity.badRequest().body(badResponse);
        } 

        ReservaDTO response = reservaServicio.crearReserva(id, codigo);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/varias_reservas")
    
    public ResponseEntity<ReservaDTO> cargarVariasReservas(@RequestBody List<ReservaDTO> requests) {
        if (requests == null || requests.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(new ReservaDTO("El array de reservas está vacío", false));
        }

        Integer idUsuario = requests.get(0).getIdUsuario();
        if (idUsuario == null) {
            return ResponseEntity.badRequest()
                .body(new ReservaDTO("El id del usuario es nulo", false));
        }
        
        for (ReservaDTO request : requests) {
            if (request.getCodigoDeBarras() == null) {
                return ResponseEntity.badRequest()
                    .body(new ReservaDTO("El código es nulo en una de las reservas", false));
            }
            // Opcional: validar que todos tengan el mismo idUsuario
            if (!idUsuario.equals(request.getIdUsuario())) {
                return ResponseEntity.badRequest()
                    .body(new ReservaDTO("Todos los id deben ser iguales", false));
            }
        }
        
        for (ReservaDTO request : requests) {
            ReservaDTO respuesta = reservaServicio.crearReserva(idUsuario, request.getCodigoDeBarras());
            if (!respuesta.isExito()) {
                return ResponseEntity.badRequest().body(respuesta);
            }
        }
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ReservaDTO("Todas las reservas se realizaron con éxito", true));
    }

    @PutMapping("/cancelar")
    public ResponseEntity<ReservaDTO> cancelarReserva(@RequestBody ReservaDTO request) {
        String idReserva = request.getCodigoReserva();
        if (idReserva == null) {
            return ResponseEntity.badRequest()
                .body(new ReservaDTO("El id de la reserva es nulo", false));
        }

        return reservaServicio.cancelarReserva(idReserva);
    }

}
