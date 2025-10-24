package com.example.biblioteca.controladorVista;

import com.example.biblioteca.Model.Usuario;
import com.example.biblioteca.Servicicos.UsuarioServicio;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;


@Controller
public class ControladorLogin {

    @Autowired
    UsuarioServicio servicio;

    @GetMapping("/login")
    public String getLogin(HttpSession session){
        Usuario user = (Usuario) session.getAttribute("user");
        if(user != null){
            return "redirect:/";
        }
        return "login";
    }
    @PostMapping("/login")
    public String login(RedirectAttributes redirect, HttpSession session, @RequestParam String codigo){
        long code = Long.parseLong(codigo);
        Optional<Usuario> optional = servicio.obtenerUsuarioPorCedula(code);
        if(optional.isPresent()){
            Usuario usuario = optional.get();
            session.setAttribute("user", usuario);
            return "redirect:/";
        }
        redirect.addFlashAttribute("error", "No se encontro el codigo");
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session){
        if(session != null){
            session.invalidate();
        }
        return "redirect:/";
    }
}
