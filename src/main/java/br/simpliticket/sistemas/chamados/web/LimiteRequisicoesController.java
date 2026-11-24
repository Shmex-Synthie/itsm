package br.simpliticket.sistemas.chamados.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Tela utilizada exclusivamente quando a listagem web excede o limite. */
@Controller
public class LimiteRequisicoesController {

    @GetMapping("/limite-requisicoes")
    public String mostrar(HttpServletRequest request, Model model) {
        Object espera = request.getAttribute("segundosEspera");
        model.addAttribute("segundosEspera", espera instanceof Number ? espera : 60);
        return "limite-requisicoes";
    }
}
