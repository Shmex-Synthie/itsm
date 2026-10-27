package br.simpliticket.sistemas.chamados.dto;

import java.util.List;
public record ChamadoDetalheResponse(ChamadoResponse chamado, List<ComentarioResponse> comentarios,
    List<HistoricoStatusResponse> historico) {
}
