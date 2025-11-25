package com.example.biblioteca.DTO;

public class RecursoBibliograficoDTO {
    private String isbn;
    private String tema;
    private String notas;
    private String estado;
    private String idioma;
    private String titulo;
    private String estante;
    private String resumen;
    private String categoria;
    private String coleccion;
    private String contenido;
    private String comentarios;
    private String descripcion;
    private String publicacion;
    private String localizacion;
    private String estadoFisico;
    private String calificacion;
    private String codigoDeBarras;
    private String tipoDePublicacion;
    private String signaturaTipografica;

    public RecursoBibliograficoDTO(String isbn, String tema, String notas, String estado, String idioma, String titulo,
            String estante, String resumen, String categoria, String coleccion, String contenido, String comentarios,
            String descripcion, String publicacion, String localizacion, String estadoFisico, String calificacion,
            String codigoDeBarras, String tipoDePublicacion, String signaturaTipografica) {

        this.isbn = isbn;
        this.tema = tema;
        this.notas = notas;
        this.estado = estado;
        this.idioma = idioma;
        this.titulo = titulo;
        this.estante = estante;
        this.resumen = resumen;
        this.categoria = categoria;
        this.coleccion = coleccion;
        this.contenido = contenido;
        this.comentarios = comentarios;
        this.descripcion = descripcion;
        this.publicacion = publicacion;
        this.localizacion = localizacion;
        this.estadoFisico = estadoFisico;
        this.calificacion = calificacion;
        this.codigoDeBarras = codigoDeBarras;
        this.tipoDePublicacion = tipoDePublicacion;
        this.signaturaTipografica = signaturaTipografica;
    }
    
    
}
