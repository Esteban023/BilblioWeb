package com.example.biblioteca.controladorVista;

import com.example.biblioteca.Model.Prestamo;
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
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/prestamo")
public class ControladorPrestamo {
    @Autowired
    PrestamoServicio servicio;

    @GetMapping("/{id}")
    public String crearPrestamo(@PathVariable String id, HttpSession session, RedirectAttributes redirect){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'del' yyyy", new Locale("es", "ES"));
        String mensajeTime = "Tiempo para reclamar: 24 horas.";
        String tituloRecurso = "";
        Usuario user = (Usuario) session.getAttribute("user");
        ResultadoPrestamo resultadoPrestamo = servicio.iniciarPrestamo(user.getId(), id);
        String tituloModal = resultadoPrestamo.getMensaje();
        Prestamo prestamo = resultadoPrestamo.getPrestamo();
        String mensaje = String.format(
                "El recurso con codigo (%s) se Prestó satisfactoriamente el %s. Por favor, devuélvalo el %s o antes.",
                prestamo.getRecursoBibliografico().getCodigoDeBarras(),
                prestamo.getFechaAdquisicion().format(formatter),
                prestamo.getFechaDevolucion().format(formatter)
        );
        redirect.addFlashAttribute("tituloModal", tituloModal);
        redirect.addFlashAttribute("mensajeTime", mensajeTime);
        redirect.addFlashAttribute("mensajeModal", mensaje);


        tituloRecurso = prestamo.getRecursoBibliografico().getTitulo();
        tituloRecurso = URLEncoder.encode(tituloRecurso, StandardCharsets.UTF_8).replace("+", "%20");

        return "redirect:/buscar/" + tituloRecurso;
    }

    @GetMapping("listar")
    public String listarPrestamos(HttpSession session, Model model){
        Usuario user =(Usuario) session.getAttribute("user");
        if(user == null) return "redirect:/";
        List<Prestamo> prestamosPorUsuario = servicio.getPrestamosPorUsuario(user.getId());
        model.addAttribute("lista", prestamosPorUsuario);
        return "pruebas";
    }

}
