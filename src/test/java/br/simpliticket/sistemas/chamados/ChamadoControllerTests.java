package br.simpliticket.sistemas.chamados;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import br.simpliticket.sistemas.chamados.controller.ChamadoController;
import br.simpliticket.sistemas.chamados.dto.ChamadoResponse;
import br.simpliticket.sistemas.chamados.entity.*;
import br.simpliticket.sistemas.chamados.exception.GlobalExceptionHandler;
import br.simpliticket.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.simpliticket.sistemas.chamados.service.ChamadoService;

class ChamadoControllerTests {
    private MockMvc mvc;
    private ChamadoService service;

    @BeforeEach
    void preparar() {
        service = mock(ChamadoService.class);
        mvc = MockMvcBuilders.standaloneSetup(new ChamadoController(service))
        .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    void criaChamado() throws Exception {
        ChamadoResponse resposta = new ChamadoResponse(1L, "Erro", "Descrição", java.time.LocalDateTime.now(), null,
            Prioridade.ALTA, StatusChamado.ABERTO, 1L, 2L, null);
        when(service.criar(any())).thenReturn(resposta);
        mvc.perform(post("/chamados").contentType(MediaType.APPLICATION_JSON)
            .content("{\"titulo\":\"Erro\",\"descricao\":\"Descrição\",\"prioridade\":\"ALTA\",\"clienteId\":1,\"categoriaId\":2}"))
        .andExpect(status().isCreated()).andExpect(header().string("Location", "/chamados/1"));
    }

    @Test
    void validaCamposObrigatorios() throws Exception {
        mvc.perform(post("/chamados").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isBadRequest());
    }

    @Test
    void retorna404ParaChamadoInexistente() throws Exception {
        when(service.buscar(99L)).thenThrow(new RecursoNaoEncontradoException("Chamado não encontrado"));
        mvc.perform(get("/chamados/99")).andExpect(status().isNotFound());
    }

}
