package com.example.biblioteca.DTO;

import com.example.biblioteca.Model.Prestamo;

public class UsuarioDTO {
    private Integer id;
    private Long cedula;
    private String rol;
    private String email;
    private String nombre;
    private boolean estado;
    private String password;
    private String apellido;
    private String telefono;

    Prestamo prestamo;

    public UsuarioDTO(Integer id, Long cedula, String rol, String email, String nombre, boolean estado, 
        String password, String apellido, String telefono) {

        this.id = id;
        this.cedula = cedula;
        this.rol = rol;
        this.email = email;
        this.nombre = nombre;
        this.estado = estado;
        this.password = password;
        this.apellido = apellido;
        this.telefono = telefono;
    }

    
}
