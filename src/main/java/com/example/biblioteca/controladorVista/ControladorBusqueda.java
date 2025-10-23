package com.example.biblioteca.controladorVista;

import com.example.biblioteca.Model.RecursoBibliografico;
import com.example.biblioteca.Servicicos.RecursoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ControladorBusqueda {
    @Autowired
    RecursoServicio servicio;

    @GetMapping("/")
    public String mostrarBusqueda(Model model){
        return  "busqueda";
    }

    @PostMapping("/buscar")
    public String mostrarResultados(Model model, @RequestParam String palabraClave){
        Optional<List<RecursoBibliografico>> recursoBibliograficos = Optional.ofNullable(servicio.buscarPorPalabraClave(palabraClave));
        List<RecursoBibliografico> resultados;
        resultados = recursoBibliograficos.orElseGet(ArrayList::new);
        model.addAttribute("palabra", palabraClave);
        model.addAttribute("resultados", resultados);
        return "resultado";
    }


    @GetMapping("/pruebas") //este controlador es solo para pruebas
    public String mostrarprueba(){
        return "pruebas";
    }

    @GetMapping("/buscar/{titulo}")
    public String mostrarRecurso(@PathVariable String titulo, Model model){
        List<RecursoBibliografico> lista = servicio.buscarPorTitulo(titulo);
        model.addAttribute("lista", lista);
        model.addAttribute("titulo", titulo);
        return "recursos";
    }
}
