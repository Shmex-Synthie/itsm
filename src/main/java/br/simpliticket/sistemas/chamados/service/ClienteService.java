package br.simpliticket.sistemas.chamados.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.simpliticket.sistemas.chamados.dto.ClienteRequest;
import br.simpliticket.sistemas.chamados.dto.ClienteResponse;
import br.simpliticket.sistemas.chamados.entity.Cliente;
import br.simpliticket.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.simpliticket.sistemas.chamados.exception.RegraNegocioException;
import br.simpliticket.sistemas.chamados.repository.ClienteRepository;

@Service 
public class ClienteService{

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ClienteResponse criar(ClienteRequest dto) {
    if (repository.existsByEmail(dto.email())) {
        throw new RegraNegocioException("Já existe cliente com o e-mail " + dto.email());
    }

    Cliente salvo = repository.save(new Cliente(dto.nome(), dto.email(), dto.telefone()));
    return ClienteResponse.de(salvo);
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
    return repository.findAll().stream().map(ClienteResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscar(Long id) {
    return ClienteResponse.de(buscarEntity(id));
    }

    @Transactional
    public ClienteResponse atualizar(Long id, ClienteRequest dto) {
    if (repository.existsByEmailAndIdNot(dto.email(), id)) {
        throw new RegraNegocioException("Já existe cliente com o e-mail " + dto.email());
    }

    Cliente cliente = buscarEntity(id);
    cliente.setNome(dto.nome());
    cliente.setEmail(dto.email());
    cliente.setTelefone(dto.telefone());
    return ClienteResponse.de(repository.save(cliente));
    }

    @Transactional
    public void excluir(Long id) {
    repository.deleteById(id);
    }

    private Cliente buscarEntity(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new RecursoNaoEncontradoException(
            "Cliente " + id + " não encontrado"));
    }
}