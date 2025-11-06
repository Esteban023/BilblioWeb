package com.example.biblioteca.Controlador;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.biblioteca.DTO.PrestamoRequest;
import com.example.biblioteca.Model.Prestamo;
import com.example.biblioteca.Model.ResultadoPrestamo;
import com.example.biblioteca.Servicicos.PrestamoServicio;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamosControlador {

    @Autowired
    PrestamoServicio prestamoServicio;

    @GetMapping("/{id}")
    public ResponseEntity<Prestamo> buscarPrestamoPorId(@PathVariable Integer id) {
        Optional<Prestamo> prestamo = prestamoServicio.buscarPrestamoPorId(id);
        
        boolean existe = prestamo.isPresent();
        if (existe) {
            return new ResponseEntity<>(prestamo.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
              
    }
    
    @PostMapping("/iniciar")
    public ResponseEntity<ResultadoPrestamo> cargarPrestamo(@RequestBody PrestamoRequest request) {
        Integer usuarioId = request.getIdUsuario();
        String codigoBarras = request.getCodigoDeBarras();

        if(usuarioId == null || codigoBarras == null) {
            ResultadoPrestamo rPrestamo = new ResultadoPrestamo(false, "Datos incorrectos");
            return ResponseEntity.badRequest().body(rPrestamo);
        }
        ResultadoPrestamo nuevoPrestamo = prestamoServicio.iniciarPrestamo(usuarioId, codigoBarras);

        return new ResponseEntity<>(nuevoPrestamo, HttpStatus.CREATED);
    }

    @PostMapping("/varios_prestamos")
    public ResponseEntity<ResultadoPrestamo> cargarVariosPrestamos(@RequestBody PrestamoRequest[] requests) {
        ResultadoPrestamo resultadoFinal = new ResultadoPrestamo(true, "Todos los prestamos se realizaron con exito");

        for (PrestamoRequest request : requests) {
            Integer usuarioId = request.getIdUsuario();
            String codigoBarras = request.getCodigoDeBarras();

            if(usuarioId == null || codigoBarras == null) {
                ResultadoPrestamo rPrestamo = new ResultadoPrestamo(false, "Datos incorrectos en uno de los prestamos");
                return ResponseEntity.badRequest().body(rPrestamo);
            }
            ResultadoPrestamo resultadoPrestamo = prestamoServicio.iniciarPrestamo(usuarioId, codigoBarras);
            boolean exito = resultadoPrestamo.isExito();
            if (!exito) {
                resultadoFinal.setExito(false);
                resultadoFinal.setMensaje("Algunos prestamos no se pudieron realizar correctamente");
            }
        }

        return new ResponseEntity<>(resultadoFinal, HttpStatus.CREATED);
    }

    @PutMapping("terminar/{id}")
    public ResponseEntity<ResultadoPrestamo> terminarPrestamo(@PathVariable Integer id) {
        if (id == null) {
            ResultadoPrestamo rPrestamo = new ResultadoPrestamo(false, "Datos incorrectos");
            return ResponseEntity.badRequest().body(rPrestamo);
        }
        ResultadoPrestamo resultadoPrestamo = prestamoServicio.finalizarPrestamo(id);

        return ResponseEntity.ok(resultadoPrestamo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Prestamo> borrarPrestamo(@PathVariable Integer id) {
        Optional<Prestamo> prestamo = prestamoServicio.borrarPrestamo(id);

        boolean isPresent = prestamo.isPresent();
        if (isPresent) {
            Prestamo prestamoEliminado = prestamo.get();
            return ResponseEntity.ok(prestamoEliminado); 
        }
        return ResponseEntity.noContent().build();
    }

}
