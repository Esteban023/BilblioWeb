package com.example.biblioteca.Servicicos;

import com.example.biblioteca.Model.Utilidades.Email;


public interface EmailServicios {

    // enviar correo
    String enviarCorreo(Email detalles);

    // enviar correo con adjunto
    String enviarCorreoConAdjunto(Email detalles);
}

