package br.simpliticket.sistemas.chamados.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.simpliticket.sistemas.chamados.dto.*;
import br.simpliticket.sistemas.chamados.service.ComentarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/chamados/{id}/comentarios")
public class ComentarioController {
    private final ComentarioService service;

    public ComentarioController(ComentarioService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ComentarioResponse> criar(@PathVariable Long id, @Valid @RequestBody ComentarioRequest dto) {
        ComentarioResponse criado = service.criar(id, dto);
        return ResponseEntity.created(URI.create("/chamados/"+id+"/comentarios/"+criado.id())).body(criado);
    }

    @GetMapping
    public List<ComentarioResponse> listar(@PathVariable Long id) {
        return service.listar(id);
    }

}
