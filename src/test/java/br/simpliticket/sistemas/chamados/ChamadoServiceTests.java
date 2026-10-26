package br.simpliticket.sistemas.chamados;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import br.simpliticket.sistemas.chamados.dto.*;
import br.simpliticket.sistemas.chamados.entity.*;
import br.simpliticket.sistemas.chamados.exception.*;
import br.simpliticket.sistemas.chamados.repository.*;
import br.simpliticket.sistemas.chamados.service.ChamadoService;

@ExtendWith(MockitoExtension.class)
class ChamadoServiceTests {
    @Mock ChamadoRepository chamados;
    @Mock ClienteRepository clientes;
    @Mock CategoriaRepository categorias;
    @Mock TecnicoRepository tecnicos;
    @Mock ComentarioRepository comentarios;
    @Mock HistoricoStatusRepository historico;
    ChamadoService service;

    @BeforeEach
    void preparar() {
        service = new ChamadoService(chamados, clientes, categorias, tecnicos, comentarios, historico);
    }

    private Chamado chamado() {
        return new Chamado("Erro", "Descrição", Prioridade.ALTA,
            new Cliente("Cliente", "cliente@email.com", null), new Categoria("Suporte", "Descrição"), null);
    }

    @Test
    void criaChamadoAbertoComDataAbertura() {
        when(clientes.findById(1L)).thenReturn(Optional.of(new Cliente("Cliente", "cliente@email.com", null)));
        when(categorias.findById(2L)).thenReturn(Optional.of(new Categoria("Suporte", "Descrição")));
        when(chamados.save(any(Chamado.class))).thenAnswer(i -> i.getArgument(0));
        ChamadoResponse resposta = service.criar(new ChamadoRequest("Erro", "Descrição", Prioridade.ALTA, 1L, 2L, null));
        assertEquals(StatusChamado.ABERTO, resposta.status());
        assertNotNull(resposta.dataAbertura());
    }

    @Test
    void fechaChamadoERegistraHistorico() {
        Chamado chamado = chamado();
        when(chamados.findById(1L)).thenReturn(Optional.of(chamado));
        when(chamados.save(chamado)).thenReturn(chamado);
        ChamadoResponse resposta = service.alterarStatus(1L, new StatusChamadoRequest(StatusChamado.FECHADO));
        assertEquals(StatusChamado.FECHADO, resposta.status());
        assertNotNull(resposta.dataFechamento());
        verify(historico).save(any(HistoricoStatus.class));
    }

    @Test
    void naoPermiteAlteracaoDeChamadoFechado() {
        Chamado chamado = chamado();
        chamado.setStatus(StatusChamado.FECHADO);
        when(chamados.findById(1L)).thenReturn(Optional.of(chamado));
        assertThrows(RegraNegocioException.class,() -> service.atribuir(1L, new AtribuirChamadoRequest(2L)));
        assertThrows(RegraNegocioException.class,() -> service.alterarStatus(1L, new StatusChamadoRequest(StatusChamado.ABERTO)));
    }

    @Test
    void naoPermiteTransicaoInvalida() {
        when(chamados.findById(1L)).thenReturn(Optional.of(chamado()));
        assertThrows(RegraNegocioException.class,() -> service.alterarStatus(1L, new StatusChamadoRequest(StatusChamado.AGUARDANDO_CLIENTE)));
    }

    @Test
    void informaChamadoInexistente() {
        when(chamados.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class,() -> service.buscar(99L));
    }

}
