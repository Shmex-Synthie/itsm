package br.simpliticket.sistemas.chamados.dto;

import java.time.LocalDateTime;
import br.simpliticket.sistemas.chamados.entity.*;
public record HistoricoStatusResponse(Long id, StatusChamado statusAnterior, StatusChamado statusNovo, LocalDateTime data) {
    public static HistoricoStatusResponse de(HistoricoStatus h) {
        return new HistoricoStatusResponse(h.getId(), h.getStatusAnterior(), h.getStatusNovo(), h.getData());
    }

}
