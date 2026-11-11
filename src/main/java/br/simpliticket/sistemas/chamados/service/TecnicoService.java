package br.simpliticket.sistemas.chamados.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.simpliticket.sistemas.chamados.dto.*;
import br.simpliticket.sistemas.chamados.entity.Tecnico;
import br.simpliticket.sistemas.chamados.exception.*;
import br.simpliticket.sistemas.chamados.repository.TecnicoRepository;

@Service
public class TecnicoService {
    private final TecnicoRepository repository;

    public TecnicoService(TecnicoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TecnicoResponse criar(TecnicoRequest dto) {
        if (repository.existsByEmail(dto.email())) {
            throw new RegraNegocioException("Já existe técnico com o e-mail " + dto.email());
        }
        return TecnicoResponse.de(repository.save(new Tecnico(dto.nome(), dto.email(), dto.perfil())));
    }

    @Transactional(readOnly = true)
    public List<TecnicoResponse> listar() {
        return repository.findAll().stream().map(TecnicoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public TecnicoResponse buscar(Long id) {
        return TecnicoResponse.de(buscarEntity(id));
    }

    @Transactional
    public TecnicoResponse atualizar(Long id, TecnicoRequest dto) {
        if (repository.existsByEmailAndIdNot(dto.email(), id)) {
            throw new RegraNegocioException("Já existe técnico com o e-mail " + dto.email());
        }
        Tecnico t = buscarEntity(id);
        t.setNome(dto.nome());
        t.setEmail(dto.email());
        t.setPerfil(dto.perfil());
        return TecnicoResponse.de(repository.save(t));
    }

    @Transactional
    public void excluir(Long id) {
        buscarEntity(id);
        repository.deleteById(id);
    }

    private Tecnico buscarEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Técnico " + id + " não encontrado"));
    }

}
