package br.simpliticket.sistemas.chamados.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.simpliticket.sistemas.chamados.dto.*;
import br.simpliticket.sistemas.chamados.entity.*;
import br.simpliticket.sistemas.chamados.exception.*;
import br.simpliticket.sistemas.chamados.repository.*;

@Service
public class ChamadoService {
    private final ChamadoRepository repository;
    private final ClienteRepository clientes;
    private final CategoriaRepository categorias;
    private final TecnicoRepository tecnicos;
    private final ComentarioRepository comentarios;
    private final HistoricoStatusRepository historico;

    public ChamadoService(ChamadoRepository repository, ClienteRepository clientes, CategoriaRepository categorias,
        TecnicoRepository tecnicos, ComentarioRepository comentarios, HistoricoStatusRepository historico) {
        this.repository = repository;
        this.clientes = clientes;
        this.categorias = categorias;
        this.tecnicos = tecnicos;
        this.comentarios = comentarios;
        this.historico = historico;
    }

    @Transactional
    public ChamadoResponse criar(ChamadoRequest dto) {
        Cliente cliente = buscarCliente(dto.clienteId());
        Categoria categoria = buscarCategoria(dto.categoriaId());
        Tecnico tecnico = dto.tecnicoId() == null ? null : buscarTecnico(dto.tecnicoId());
        return ChamadoResponse.de(repository.save(new Chamado(dto.titulo(), dto.descricao(), dto.prioridade(), cliente, categoria, tecnico)));
    }

    @Transactional(readOnly = true)
    public Page<ChamadoResponse> listar(StatusChamado status, Prioridade prioridade, Long clienteId, Pageable pageable) {
        return repository.filtrar(status, prioridade, clienteId, pageable).map(ChamadoResponse::de);
    }

    @Transactional(readOnly = true)
    public Page<ChamadoResponse> listarPorCliente(Long id, Pageable pageable) {
        buscarCliente(id);
        return repository.findByClienteId(id, pageable).map(ChamadoResponse::de);
    }

    @Transactional(readOnly = true)
    public ChamadoResponse buscar(Long id) {
        return ChamadoResponse.de(buscarEntity(id));
    }

    @Transactional(readOnly = true)
    public ChamadoDetalheResponse detalhe(Long id) {
        Chamado chamado = buscarEntity(id);
        List<ComentarioResponse> lista = comentarios.findByChamadoIdOrderByDataAsc(id).stream().map(ComentarioResponse::de).toList();
        List<HistoricoStatusResponse> mudancas = historico.findByChamadoIdOrderByDataAsc(id).stream().map(HistoricoStatusResponse::de).toList();
        return new ChamadoDetalheResponse(ChamadoResponse.de(chamado), lista, mudancas);
    }

    @Transactional
    public ChamadoResponse atualizar(Long id, ChamadoRequest dto) {
        Chamado c = buscarEntity(id);
        verificarAberto(c);
        Cliente cliente = buscarCliente(dto.clienteId());
        Categoria categoria = buscarCategoria(dto.categoriaId());
        Tecnico tecnico = dto.tecnicoId() == null ? null : buscarTecnico(dto.tecnicoId());
        c.setTitulo(dto.titulo());
        c.setDescricao(dto.descricao());
        c.setPrioridade(dto.prioridade());
        c.setCliente(cliente);
        c.setCategoria(categoria);
        c.setTecnico(tecnico);
        return ChamadoResponse.de(repository.save(c));
    }

    @Transactional
    public ChamadoResponse atribuir(Long id, AtribuirChamadoRequest dto) {
        Chamado c = buscarEntity(id);
        verificarAberto(c);
        c.setTecnico(buscarTecnico(dto.tecnicoId()));
        return ChamadoResponse.de(repository.save(c));
    }

    @Transactional
    public ChamadoResponse alterarStatus(Long id, StatusChamadoRequest dto) {
        Chamado c = buscarEntity(id);
        verificarAberto(c);
        StatusChamado anterior = c.getStatus();
        StatusChamado novo = dto.status();
        if (anterior == novo) {
            throw new RegraNegocioException("O chamado já está com este status");
        }
        if (!transicaoValida(anterior, novo)) {
            throw new RegraNegocioException("Transição de status inválida");
        }
        c.setStatus(novo);
        if (novo == StatusChamado.FECHADO) {
            c.setDataFechamento(LocalDateTime.now());
        }
        Chamado salvo = repository.save(c);
        historico.save(new HistoricoStatus(salvo, anterior, novo));
        return ChamadoResponse.de(salvo);
    }

    private boolean transicaoValida(StatusChamado atual, StatusChamado novo) {
        return switch (atual) {
            case ABERTO -> novo == StatusChamado.EM_ATENDIMENTO || novo == StatusChamado.FECHADO;
            case EM_ATENDIMENTO -> novo == StatusChamado.AGUARDANDO_CLIENTE || novo == StatusChamado.FECHADO;
            case AGUARDANDO_CLIENTE -> novo == StatusChamado.EM_ATENDIMENTO || novo == StatusChamado.FECHADO;
            case FECHADO -> false;
        };
    }

    @Transactional
    public void excluir(Long id) {
        buscarEntity(id);
        comentarios.deleteByChamadoId(id);
        historico.deleteByChamadoId(id);
        repository.deleteById(id);
    }

    private void verificarAberto(Chamado c) {
        if (c.getStatus() == StatusChamado.FECHADO) {
            throw new RegraNegocioException("Não é permitido alterar chamado fechado");
        }
    }

    public Chamado buscarEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Chamado " + id + " não encontrado"));
    }

    private Cliente buscarCliente(Long id) {
        return clientes.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Cliente " + id + " não encontrado"));
    }

    private Categoria buscarCategoria(Long id) {
        return categorias.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Categoria " + id + " não encontrada"));
    }

    private Tecnico buscarTecnico(Long id) {
        return tecnicos.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Técnico " + id + " não encontrado"));
    }

}
