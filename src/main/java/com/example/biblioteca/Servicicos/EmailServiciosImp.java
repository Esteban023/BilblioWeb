package com.example.biblioteca.Servicicos;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.biblioteca.Model.Utilidades.Email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service

public class EmailServiciosImp implements EmailServicios {

    @Autowired private JavaMailSender javaMailSender;

    private String sender = "Biblioteca Univalle <alejandroxnoguera@gmail.com>";

    @Override
    public String enviarCorreo(Email detalles)
    {
         MimeMessage msjCorreo
            = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
        try {
            
            
            // Poner multipart como true para permitir el envio de adjuntos
            mimeMessageHelper
            = new MimeMessageHelper(msjCorreo, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(detalles.getRecipiente());
            mimeMessageHelper.setText(detalles.getMsgBody());
            mimeMessageHelper.setSubject(
                detalles.getAsunto());
            // Enviando el correo
            javaMailSender.send(msjCorreo);
            return "Correo enviado con exito";
        }

        catch (Exception e) {
            return "Error enviando correo";
        }
    }

    @Override
    public String
    enviarCorreoConAdjunto(Email detalles)
    {
        // Crear mime message. usado para crear correos con archivos adjuntos
        MimeMessage mimeMessage
            = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {

            // Poner multipart como true para permitir el envio de adjuntos
            mimeMessageHelper
                = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(detalles.getRecipiente());
            mimeMessageHelper.setText(detalles.getMsgBody());
            mimeMessageHelper.setSubject(
                detalles.getAsunto());

            // Añadiendo el adjunto
            FileSystemResource file
                = new FileSystemResource(
                    new File(detalles.getAdjunto()));

            mimeMessageHelper.addAttachment(
                file.getFilename(), file);

            // Enviando el correo
            javaMailSender.send(mimeMessage);
            return "Correo enviado con exito";
        }

        catch (MessagingException e) {

            return "Error enviando el correo";
        }
    }
}