package com.example.biblioteca.controladorVista;

import com.example.biblioteca.Controlador.ReservaControlador;
import com.example.biblioteca.Model.Prestamo;
import com.example.biblioteca.Model.ResultadoPrestamo;
import com.example.biblioteca.Model.Usuario;
import com.example.biblioteca.Model.Utilidades.Email;
import com.example.biblioteca.Servicicos.EmailServiciosImp;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/prestamo")
public class ControladorPrestamo {
    @Autowired
    PrestamoServicio prestamoServicio;

    @Autowired
    EmailServiciosImp emailServicio;

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
        String mensajePrestamo = buildMensajePrestamo(prestamo, formatter);

        redirect.addFlashAttribute("tituloModal", resultado.getMensaje());
        redirect.addFlashAttribute("mensajeTime", "Tiempo para reclamar: 24 horas.");
        redirect.addFlashAttribute("mensajeModal", mensajePrestamo);

        // Generar URL segura para búsqueda
        String recursoCodificado = encodeTitulo(
            prestamo.getRecursoBibliografico().getTitulo()
        );

        // Enviar correo
        enviarCorreoPrestamo(prestamo, resultado);

        return "redirect:/buscar/" + recursoCodificado;
    }

    private String buildMensajePrestamo(Prestamo prestamo, DateTimeFormatter formatter) {
        return String.format(
            "El recurso con código (%s) se prestó satisfactoriamente el %s. "
            + "Por favor, devuélvalo el %s o antes.",
            prestamo.getRecursoBibliografico().getTitulo(),
            prestamo.getRecursoBibliografico().getCodigoDeBarras(),
            prestamo.getFechaAdquisicion().format(formatter),
            prestamo.getFechaDevolucion().format(formatter)
        );
    }

    private String encodeTitulo(String titulo) {
        return URLEncoder.encode(titulo, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private void enviarCorreoPrestamo(Prestamo prestamo, ResultadoPrestamo resultado) {
        String bodyMail = prestamoServicio.generarBodyMail(resultado);
        Email email = new Email(
            prestamo.getUsuario().getEmail(),
            bodyMail,
            "Préstamo de recurso Bibliográfico",
            null
        );
        emailServicio.enviarCorreo(email);
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

        Integer idUser = user.getId();
        List<String> errores = new ArrayList<>();

        for (String codigo : codigos) {
            if (codigo != null || codigo.isBlank()) continue;

            ResultadoPrestamo resultado = prestamoServicio.iniciarPrestamo(idUser, codigo);

            if (!resultado.isExito()) {
                errores.add("• " + codigo + ": " + resultado.getMensaje());
            }
        }

        if (errores.isEmpty()) {
            redirect.addFlashAttribute("success", "Todos los recursos se prestaron exitosamente.");
        } else {
            redirect.addFlashAttribute("error",
            "Algunos recursos no se pudieron prestar:<br>" + String.join("<br>", errores));
        }

        return "redirect:/canasta/listar";
    }

}
