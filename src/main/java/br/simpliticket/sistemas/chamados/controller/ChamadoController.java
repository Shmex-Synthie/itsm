package br.simpliticket.sistemas.chamados.controller;

import java.net.URI;

import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.simpliticket.sistemas.chamados.dto.*;
import br.simpliticket.sistemas.chamados.entity.*;
import br.simpliticket.sistemas.chamados.service.ChamadoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/chamados")
public class ChamadoController {
    private final ChamadoService service;

    public ChamadoController(ChamadoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ChamadoResponse> criar(@Valid @RequestBody ChamadoRequest dto) {
        ChamadoResponse criado = service.criar(dto);
        return ResponseEntity.created(URI.create("/chamados/"+criado.id())).body(criado);
    }

    @GetMapping
    public Page<ChamadoResponse> listar(@RequestParam(required = false) StatusChamado status,
        @RequestParam(required = false) Prioridade prioridade, @RequestParam(required = false) Long clienteId,
        @PageableDefault(size = 20, sort = "dataAbertura", direction = Sort.Direction.DESC) Pageable pageable)
    {
        return service.listar(status, prioridade, clienteId, pageable);
    }

    @GetMapping("/{id}")
    public ChamadoResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @GetMapping("/{id}/detalhe")
    public ChamadoDetalheResponse detalhe(@PathVariable Long id) {
        return service.detalhe(id);
    }

    @PutMapping("/{id}")
    public ChamadoResponse atualizar(@PathVariable Long id, @Valid @RequestBody ChamadoRequest dto) {
        return service.atualizar(id, dto);
    }

    @PatchMapping("/{id}/atribuir")
    public ChamadoResponse atribuir(@PathVariable Long id, @Valid @RequestBody AtribuirChamadoRequest dto) {
        return service.atribuir(id, dto);
    }

    @PatchMapping("/{id}/status")
    public ChamadoResponse status(@PathVariable Long id, @Valid @RequestBody StatusChamadoRequest dto) {
        return service.alterarStatus(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

}
