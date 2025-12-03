
package com.example.biblioteca.controladorVista;

import com.example.biblioteca.DTO.ReservaDTO;
import com.example.biblioteca.Model.RecursoBibliografico;
import com.example.biblioteca.Model.Reserva;
import com.example.biblioteca.Model.Usuario;
import com.example.biblioteca.Servicicos.ReservaServicio;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



@Controller
@RequestMapping("/reserva")
public class ControladorReserva {
    @Autowired
    ReservaServicio servicio;
    
    public static final Logger logger = LoggerFactory.getLogger(ControladorReserva.class);
    
    @GetMapping("/{id}")
    public String crearReserva(@PathVariable String id, HttpSession session, RedirectAttributes redirect, HttpServletRequest request){
        String tituloRecurso = "";
        Usuario user = (Usuario) session.getAttribute("user");
        ReservaDTO crearReserva = servicio.crearReserva(user.getId(), id);
        
        if(!crearReserva.isExito()){
            redirect.addFlashAttribute("error", crearReserva.getMensaje());
            // Obtener la página anterior del header Referer
            String referer = request.getHeader("Referer");
            
            return "redirect:" + (referer != null ? referer : "/");
        }
        
        String tituloModal = crearReserva.getMensaje();
        Reserva reserva = crearReserva.getReserva();
        String mensaje = String.format(
                "Has reservado (%s) para el %s. Serás notificado en tu correo cuando el recurso esté listo para ser recogido",
                reserva.getRecursoBibliografico().getTitulo(),
                reserva.getFechaReserva()
        );
        redirect.addFlashAttribute("tituloModal", tituloModal);
        redirect.addFlashAttribute("mensajeModal", mensaje);

        // Formateador reutilizable
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'del' yyyy", 
        Locale.of("es", "ES")
    );
    
        //enviar mail
        servicio.enviarCorreoReserva(reserva, formatter);
        
        tituloRecurso = reserva.getRecursoBibliografico().getTitulo();
        tituloRecurso = URLEncoder.encode(tituloRecurso, StandardCharsets.UTF_8).replace("+", "%20");
        return "redirect:/buscar/" + tituloRecurso;
    }

    @GetMapping("/varios")
    public String reservarVarias(@ModelAttribute("select") List<String> select,
                                 HttpSession session,RedirectAttributes redirect) {
        
                                    
        Usuario user = (Usuario) session.getAttribute("user");
        if(user == null) {
            return "redirect:/";
        }


        Integer idUser = user.getId();
        String mensajeModal = null;
        List<String> errores = new ArrayList<>();
        List<Reserva> realizados = new ArrayList<>(); 
        //lista donde se guardara los recursos que se deben eliminar de la canasta
        List<RecursoBibliografico> eliminarCanasta = new ArrayList<>();
        
        for (String codigo : select){
            if(codigo == null || codigo.trim().isEmpty()) continue;
            
            ReservaDTO resultado = servicio.crearReserva(idUser, codigo);
            if(!resultado.isExito()) {
                errores.add(resultado.getMensaje());
            }else{
                realizados.add(resultado.getReserva());
                eliminarCanasta.add(resultado.getReserva().getRecursoBibliografico());
            }
        }
        
        if(errores.isEmpty()) {
            mensajeModal = "Todos los recursos fueron reservados exitosamente. Se le ha enviado un correo con la información completa.";
        }else if(errores.size() < select.size()){
            mensajeModal = "Algunos recursos no se pudieron reservar. Se le ha enviado un correo con la información completa.";
        }

        redirect.addFlashAttribute("errors", errores);

        //generar mensaje para el modal
        redirect.addFlashAttribute("tituloModal", "Reservas realizadas.");
        redirect.addFlashAttribute("mensajeModal", mensajeModal);

        //enviar mail con todas las reservas exitosas
        servicio.enviarCorreoVariasReservas(realizados, user);

        //se agrega un atributo redirect con los recursos que se deben eliminar de la canasta
        redirect.addFlashAttribute("eliminarCanasta", eliminarCanasta);

        return "redirect:/canasta/listar";
    }
    
    @GetMapping("/listar")
    public String listarReservas(HttpSession session, Model model){
        Usuario user =(Usuario) session.getAttribute("user");
        if(user == null) return "redirect:/";
        List<Reserva> reservasPorUsuario = servicio.getPorUsuario(user.getId());
        model.addAttribute("reservas", reservasPorUsuario);
        return "pruebas";
    }

    @GetMapping("/cancelar/{codigoReserva}")
    public String cancelarReserva(@PathVariable String codigoReserva, RedirectAttributes redirect){
        if(servicio.cancelarReserva(codigoReserva).getBody().isExito()){
            return "redirect:/reserva/listar";
        }
        redirect.addFlashAttribute("error", "No se pudo cancelar la reserva");
        return "redirect:/reserva/listar";
    }
}
