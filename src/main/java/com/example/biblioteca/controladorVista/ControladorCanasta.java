package com.example.biblioteca.controladorVista;

import com.example.biblioteca.Model.RecursoBibliografico;
import com.example.biblioteca.Servicicos.RecursoServicio;
import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/canasta")
public class ControladorCanasta {
    
    @Autowired
    RecursoServicio servicio;
    
    @PostMapping("/agregar")
    public String agregarRecurso(@RequestParam String select, HttpSession session){
        if(select == null ) return "redirect:/";
        List<RecursoBibliografico> lista;
        Optional<RecursoBibliografico> opt = servicio.obtenerRecursoBibliograficoCodigoDeBarras(select);
        if(opt.isEmpty()) return "redirect:/";
        lista = (ArrayList)session.getAttribute("canasta");
        if(lista == null) lista = new ArrayList<>();
        RecursoBibliografico recurso = opt.get();
        lista.add(recurso);
        session.setAttribute("canasta", lista);
        String tituloRecurso = recurso.getTitulo();
        tituloRecurso = URLEncoder.encode(tituloRecurso, StandardCharsets.UTF_8).replace("+", "%20");
        
        return "redirect:/buscar/" + tituloRecurso;
    }
    
    @GetMapping("/listar")
    public String verCanasta(HttpSession session, Model model){
        List<RecursoBibliografico> lista = (ArrayList)session.getAttribute("canasta");
        if(lista == null) lista = new ArrayList<>();
        else lista = servicio.buscarVarios(lista);
        
        
        
        model.addAttribute("resultados", lista);
        return "canasta";
        
    }
}
