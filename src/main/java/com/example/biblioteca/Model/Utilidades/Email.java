package com.example.biblioteca.Model.Utilidades;


public class Email {
    private boolean html;
    private String recipiente;
    private String msgBody;
    private String asunto;
    private String adjunto;
    
    // Constructor sin parámetros (OBLIGATORIO)
    public Email() {}
    
    // Constructor con parámetros
    public Email(String recipiente, String msgBody, String asunto, String adjunto, boolean html) {
        this.recipiente = recipiente;
        this.msgBody = msgBody;
        this.asunto = asunto;
        this.adjunto = adjunto;
        this.html = html;
    }
    
    // GETTERS
    public String getRecipiente() { return recipiente; }
    public String getMsgBody() { return msgBody; }
    public String getAsunto() { return asunto; }
    public String getAdjunto() { return adjunto; }
    public boolean isHtml() { return html; }
    
    // SETTERS
    public void setRecipiente(String recipiente) { this.recipiente = recipiente; }
    public void setMsgBody(String msgBody) { this.msgBody = msgBody; }
    public void setAsunto(String asunto) { this.asunto = asunto; }
    public void setAdjunto(String adjunto) { this.adjunto = adjunto; }
    public void setHtml(boolean html) { this.html = html; }
}