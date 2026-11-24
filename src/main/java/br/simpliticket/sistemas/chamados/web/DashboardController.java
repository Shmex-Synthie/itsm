package br.simpliticket.sistemas.chamados.web;

import br.simpliticket.sistemas.chamados.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller

public class DashboardController {

    private final ClienteRepository clienteRepository;
    private final TecnicoRepository tecnicoRepository;
    private final ChamadoRepository chamadoRepository;
    private final CategoriaRepository categoriaRepository;

    public DashboardController(ClienteRepository clienteRepository, TecnicoRepository tecnicoRepository,
    ChamadoRepository chamadoRepository, CategoriaRepository categoriaRepository) {
        this.clienteRepository = clienteRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.chamadoRepository = chamadoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("nomeSistema", "SimpliTicket");
        model.addAttribute("mensagem", "Bem-vindo ao SimpliTicket.");
        model.addAttribute("totalChamados", chamadoRepository.count());
        model.addAttribute("totalClientes", clienteRepository.count());
        model.addAttribute("totalTecnicos", tecnicoRepository.count());
        model.addAttribute("categorias", categoriaRepository.count());
        return "dashboard";
    }

    @GetMapping("/sobre")
    public String sobre() {return "sobre";}

}
