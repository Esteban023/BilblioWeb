package com.example.biblioteca.Model.Utilidades;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Anotaciones
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Email {

    private String recipiente;
    private String msgBody;
    private String asunto;
    private String adjunto;
    private boolean html;
    
}
