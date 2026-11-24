package br.simpliticket.sistemas.chamados.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.simpliticket.sistemas.chamados.dto.CategoriaRequest;
import br.simpliticket.sistemas.chamados.dto.CategoriaResponse;
import br.simpliticket.sistemas.chamados.entity.Categoria;
import br.simpliticket.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.simpliticket.sistemas.chamados.exception.RegraNegocioException;
import br.simpliticket.sistemas.chamados.repository.CategoriaRepository;

@Service 
public class CategoriaService{

    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CategoriaResponse criar(CategoriaRequest dto) {
    if (repository.existsByNome(dto.nome())) {
        throw new RegraNegocioException("Já existe uma categoria com o nome " + dto.nome());
    }

    Categoria salvo = repository.save(new Categoria(dto.nome(), dto.descricao()));
    return CategoriaResponse.de(salvo);
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
    return repository.findAll().stream().map(CategoriaResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse buscar(Long id) {
    return CategoriaResponse.de(buscarEntity(id));
    }

    @Transactional
    public CategoriaResponse atualizar(Long id, CategoriaRequest dto) {
    if (repository.existsByNomeAndIdNot(dto.nome(), id)) {
        throw new RegraNegocioException("Já existe uma categoria com o nome " + dto.nome());
    }

    Categoria categoria = buscarEntity(id);
    categoria.setNome(dto.nome());
    categoria.setDescricao(dto.descricao());
    return CategoriaResponse.de(repository.save(categoria));
    }

    @Transactional
    public void excluir(Long id) {
    repository.deleteById(id);
    }

    private Categoria buscarEntity(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new RecursoNaoEncontradoException(
            "Categoria " + id + " não encontrada"));
    }
}