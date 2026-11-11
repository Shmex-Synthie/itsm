package br.simpliticket.sistemas.chamados.dto;

import java.time.LocalDateTime;
import br.simpliticket.sistemas.chamados.entity.*;

public record ChamadoResponse(Long id, String titulo, String descricao, LocalDateTime dataAbertura,
    LocalDateTime dataFechamento, Prioridade prioridade, StatusChamado status,
    Long clienteId, Long categoriaId, Long tecnicoId) {
    public static ChamadoResponse de(Chamado c) {
        return new ChamadoResponse(c.getId(), c.getTitulo(), c.getDescricao(),
            c.getDataAbertura(), c.getDataFechamento(), c.getPrioridade(), c.getStatus(), c.getCliente().getId(),
            c.getCategoria().getId(), c.getTecnico() == null ? null : c.getTecnico().getId());
    }

}
