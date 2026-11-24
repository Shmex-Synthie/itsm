package br.simpliticket.sistemas.chamados.controller;

import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import br.simpliticket.sistemas.chamados.dto.ChamadoResponse;
import br.simpliticket.sistemas.chamados.service.ChamadoService;

@RestController
@RequestMapping("/clientes/{id}/chamados")
public class ClienteChamadoController {
    private final ChamadoService service;

    public ClienteChamadoController(ChamadoService service) {
        this.service = service;
    }

    @GetMapping
    public Page<ChamadoResponse> listar(@PathVariable Long id,
        @PageableDefault(size = 20, sort = "dataAbertura", direction = Sort.Direction.DESC) Pageable pageable)
    {
        return service.listarPorCliente(id, pageable);
    }

}
