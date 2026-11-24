package br.simpliticket.sistemas.chamados.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.simpliticket.sistemas.chamados.dto.*;
import br.simpliticket.sistemas.chamados.entity.*;
import br.simpliticket.sistemas.chamados.repository.ComentarioRepository;

@Service
public class ComentarioService {
    private final ComentarioRepository repository;
    private final ChamadoService chamados;

    public ComentarioService(ComentarioRepository repository, ChamadoService chamados) {
        this.repository = repository;
        this.chamados = chamados;
    }

    @Transactional
    public ComentarioResponse criar(Long chamadoId, ComentarioRequest dto) {
        Chamado chamado = chamados.buscarEntity(chamadoId);
        return ComentarioResponse.de(repository.save(new Comentario(dto.texto(), dto.autor(), chamado)));
    }

    @Transactional(readOnly = true)
    public List<ComentarioResponse> listar(Long chamadoId) {
        chamados.buscarEntity(chamadoId);
        return repository.findByChamadoIdOrderByDataAsc(chamadoId).stream().map(ComentarioResponse::de).toList();
    }

}
