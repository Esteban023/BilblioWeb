
package com.example.biblioteca.controladorVista;

import com.example.biblioteca.DTO.ReservaDTO;
import com.example.biblioteca.Model.Reserva;
import com.example.biblioteca.Model.Usuario;
import com.example.biblioteca.Servicicos.ReservaServicio;
import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
    public String crearReserva(@PathVariable String id, HttpSession session, RedirectAttributes redirect){
        String tituloRecurso = "";
        Usuario user = (Usuario) session.getAttribute("user");
        ReservaDTO crearReserva = servicio.crearReserva(user.getId(), id);
        if(!crearReserva.isExito()){
            logger.info(crearReserva.getMensaje());
            return "redirect:/";
        }
        String tituloModal =  crearReserva.getMensaje();
        Reserva reserva = crearReserva.getReserva();
        String mensaje = String.format(
                "El recurso con codigo (%s) se Reservó satisfactoriamente para el %s. Sera notificado cuando el recurso este listo para ser recogido",
                reserva.getRecursoBibliografico().getCodigoDeBarras(),
                reserva.getFechaReserva()
        );
        redirect.addFlashAttribute("tituloModal", tituloModal);
        redirect.addFlashAttribute("mensajeModal", mensaje);
        
        tituloRecurso = reserva.getRecursoBibliografico().getTitulo();
        tituloRecurso = URLEncoder.encode(tituloRecurso, StandardCharsets.UTF_8).replace("+", "%20");
        return "redirect:/buscar/" + tituloRecurso;
    }
}
