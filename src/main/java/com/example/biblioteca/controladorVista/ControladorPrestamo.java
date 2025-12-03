package com.example.biblioteca.controladorVista;

import com.example.biblioteca.Model.Prestamo;
import com.example.biblioteca.Model.RecursoBibliografico;
import com.example.biblioteca.Model.ResultadoPrestamo;
import com.example.biblioteca.Model.Usuario;
import com.example.biblioteca.Servicicos.PrestamoServicio;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.web.bind.annotation.ModelAttribute;


@Controller
@RequestMapping("/prestamo")
public class ControladorPrestamo {
    @Autowired
    PrestamoServicio prestamoServicio;

    @GetMapping("/{id}")
    public String crearPrestamo(@PathVariable String id,
                                HttpSession session,
                                RedirectAttributes redirect) {

        Usuario user = (Usuario) session.getAttribute("user");
        ResultadoPrestamo resultado = prestamoServicio.iniciarPrestamo(user.getId(), id);

        if (!resultado.isExito()) {
            redirect.addFlashAttribute("error", resultado.getMensaje());
            return "redirect:/";
        }

        Prestamo prestamo = resultado.getPrestamo();

        // Formateador reutilizable
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'del' yyyy", 
            Locale.of("es", "ES")
        );

        // Construcción de mensajes
        String mensajePrestamo = prestamoServicio.buildMensajePrestamo(prestamo, formatter);

        redirect.addFlashAttribute("tituloModal", resultado.getMensaje());
        redirect.addFlashAttribute("mensajeTime", "Tiempo para reclamar: 24 horas.");
        redirect.addFlashAttribute("mensajeModal", mensajePrestamo);

        // Generar URL segura para búsqueda
        String recursoCodificado = encodeTitulo(
            prestamo.getRecursoBibliografico().getTitulo()
        );

        // Enviar correo
        prestamoServicio.enviarCorreoPrestamo(prestamo, formatter);

        return "redirect:/buscar/" + recursoCodificado;
    }

    private String encodeTitulo(String titulo) {
        return URLEncoder.encode(titulo, StandardCharsets.UTF_8).replace("+", "%20");
    }

    @GetMapping("listar")
    public String listarPrestamos(HttpSession session, Model model){
        Usuario user =(Usuario) session.getAttribute("user");
        if(user == null) return "redirect:/";
        List<Prestamo> prestamosPorUsuario = prestamoServicio.getPrestamosPorUsuario(user.getId());
        model.addAttribute("prestamos", prestamosPorUsuario);
        return "pruebas";
    }
    
   @GetMapping("/varios")
    public String prestarVarios(@ModelAttribute("select") List<String> codigos,
                                HttpSession session,
                                RedirectAttributes redirect) 
    {
        Usuario user = (Usuario) session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }

        if (codigos == null || codigos.isEmpty()) {
            redirect.addFlashAttribute("error", "No seleccionaste ningún recurso.");
            return "redirect:/canasta/listar";
        }

        String mensajeModal = null;
        Integer idUser = user.getId();
        List<String> errores = new ArrayList<>();
        List<Prestamo> realizados = new ArrayList<>();
        //lista donde se guardara los recursos que se deben eliminar de la canasta
        List<RecursoBibliografico> eliminarCanasta = new ArrayList<>();

        for (String codigo : codigos) {
            if (codigo == null || codigo.isBlank()) continue;

            ResultadoPrestamo resultado = prestamoServicio.iniciarPrestamo(idUser, codigo);

            if (!resultado.isExito()) {
                errores.add(resultado.getMensaje());
            }else{
                realizados.add(resultado.getPrestamo());
                eliminarCanasta.add(resultado.getPrestamo().getRecursoBibliografico());
            }
        }

        if (errores.isEmpty()) {
            mensajeModal = "Todos los recursos fueron prestados exitosamente. Se le ha enviado un correo con la información completa.";
        }else if(errores.size()< codigos.size()){
            mensajeModal = "Algunos recursos no se pudieron prestar. Se le ha enviado un correo con la información completa.";
        }
        redirect.addFlashAttribute("errors", errores);
        
        //generar mensaje para el modal
        redirect.addFlashAttribute("tituloModal", "Prestamos realizados.");
        redirect.addFlashAttribute("mensajeTime", "Tiempo para reclamar: 24 horas.");
        redirect.addFlashAttribute("mensajeModal", mensajeModal);

        //se envia los mails de los prestamos exitosos
        if(!realizados.isEmpty()) prestamoServicio.enviarCorreoVariosPrestamos(realizados, user);

        //se agrega un atributo redirect con los recursos que se deben eliminar de la canasta
        redirect.addFlashAttribute("eliminarCanasta", eliminarCanasta);

        return "redirect:/canasta/listar";
    }

}
