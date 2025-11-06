package com.example.biblioteca.Controlador;

import com.example.biblioteca.DTO.ReservaDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.biblioteca.Servicicos.ReservaServicio;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
