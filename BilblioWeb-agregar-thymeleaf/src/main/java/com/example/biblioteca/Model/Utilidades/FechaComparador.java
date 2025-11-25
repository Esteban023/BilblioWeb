package com.example.biblioteca.Model.Utilidades;

import java.time.LocalDateTime;
import java.util.Comparator;

import com.example.biblioteca.Model.Reserva;

public class FechaComparador implements Comparator<Reserva> {
    @Override
    public int compare(Reserva r1, Reserva r2) {
        LocalDateTime fecha1 = r1.getFechaReserva();
        LocalDateTime fecha2 = r2.getFechaReserva();
        return fecha1.compareTo(fecha2);
    }
 
    
}
