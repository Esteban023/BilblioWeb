package com.example.biblioteca.Controlador;

// Importing required classes
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.biblioteca.Model.Utilidades.Email;
import com.example.biblioteca.Servicicos.EmailServicios;

// Annotation
@RestController
// Class
public class EmailControlador {
    @Autowired private EmailServicios emailServicios;

    // Sending a simple Email
    @PostMapping("/enviarCorreo")
    public String
    enviarCorreo(@RequestBody Email detalles)
    {
        String status
            = emailServicios.enviarCorreo(detalles);

        return status;
    }

    // Sending email with attachment
    @PostMapping("/eviarCorreoConAdjunto")
    public String enviarCorreoConAdjunto(
        @RequestBody Email detalles)
    {
        String status
            = emailServicios.enviarCorreoConAdjunto(detalles);

        return status;
    }
}