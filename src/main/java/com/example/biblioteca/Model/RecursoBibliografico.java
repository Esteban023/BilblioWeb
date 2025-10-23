package com.example.biblioteca.Model;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;


@Entity
public class RecursoBibliografico {

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "isbn", nullable = true, unique = true)
    private String isbn;

    @Id
    @Column(name = "codigo_de_barras", nullable = false, unique = true, length = 100)
    private String codigoDeBarras;

    @Column(name = "localizacion", nullable = false, length = 50)
    private String localizacion;

    @Column(name = "estante", nullable = false, length = 100)
    private String estante;

    @Column(name = "signatura_tipografica", nullable = false, unique = true, length = 100)
    private String signaturaTipografica;

    @Column(name = "coleccion", nullable = false)
    private String coleccion;

    @Column(name = "idioma", nullable = false, length = 50)
    private String idioma;

    @Column(name = "publicacion", nullable = true, length = 50)
    private String publicacion;

    @Column(name = "tema", nullable = false, length = 100)
    private String tema;

    @Column(name = "estado", nullable = false, length = 50)
    private String estado;

    @Column(name = "categoria", nullable = false, length = 100)
    private String categoria;

    @Column(name = "descripcion", length = 150)
    private String descripcion;

    @Column(name = "tipo_de_publicacion", nullable = false)
    private String tipoDePublicacion;

    @Column(name = "estado_fisico", nullable = false, length = 50)
    private String estadoFisico;

    @Column(name = "contenido", length = 100)
    private String contenido;
  
    @Column(name = "resumen", length = 500)
    private String resumen;

    @Column(name = "Notas", length = 200)
    private String notas;

    @Column(name = "calificacion", length = 100) //Calificación del libro
    private String calificacion;

    @Column(name = "comentarios", length = 200) 
    private String comentarios;

    @ManyToMany(
        cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
        name = "autores_recursos_bibliograficos", 
        joinColumns = @JoinColumn(name = "codigo_de_barras"),
        inverseJoinColumns = @JoinColumn(name = "autor_id")
    )
    private Set<Autor> autores = new HashSet<>();

    public RecursoBibliografico() {

    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCodigoDeBarras() {
        return codigoDeBarras;
    }

    public void setCodigoDeBarras(String codigoDeBarras) {
        this.codigoDeBarras = codigoDeBarras;
    }

    public String getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(String localizacion) {
        this.localizacion = localizacion;
    }

    public String getEstante() {
        return estante;
    }

    public void setEstante(String estante) {
        this.estante = estante;
    }

    public String getSignaturaTipografica() {
        return signaturaTipografica;
    }

    public void setSignaturaTipografica(String signaturaTipografica) {
        this.signaturaTipografica = signaturaTipografica;
    }

    public String getColeccion() {
        return coleccion;
    }

    public void setColeccion(String coleccion) {
        this.coleccion = coleccion;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public String getPublicacion() {
        return publicacion;
    }

    public void setPublicacion(String publicacion) {
        this.publicacion = publicacion;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipoDePublicacion() {
        return tipoDePublicacion;
    }

    public void setTipoDePublicacion(String tipoDePublicacion) {
        this.tipoDePublicacion = tipoDePublicacion;
    }

    public String getEstadoFisico() {
        return estadoFisico;
    }

    public void setEstadoFisico(String estadoFisico) {
        this.estadoFisico = estadoFisico;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public String getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(String calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public Set<Autor> getAutores() {
        return autores;
    }

    public String getAutoresString(){
        String lista = "";
        if(!autores.isEmpty()) {
            lista = autores.stream().map(a -> a.getNombre() + " " + a.getApellido()).collect(Collectors.joining(";"));
        }
        return lista;
    }

    public void setAutores(Set<Autor> autores) {
        this.autores = autores;
    }

    

    
}
