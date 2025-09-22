package com.example.biblioteca.Model;

import java.io.Serializable;
import java.util.Objects;

public class UsuarioId implements Serializable {
    private Integer id;
    private Long cedula;

    // Default constructor
    public UsuarioId() {}

    // Parameterized constructor
    public UsuarioId(Integer id, Long cedula) {
        this.id = id;
        this.cedula = cedula;
    }

    // getters, setters, equals y hashCode
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Long getCedula() { return cedula; }
    public void setCedula(Long cedula) { this.cedula = cedula; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsuarioId)) return false;
        UsuarioId that = (UsuarioId) o;
        return Objects.equals(id, that.id) && Objects.equals(cedula, that.cedula);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cedula);
    }

}
