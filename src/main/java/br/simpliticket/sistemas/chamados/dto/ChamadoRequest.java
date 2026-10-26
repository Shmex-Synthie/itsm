package br.simpliticket.sistemas.chamados.dto;

import br.simpliticket.sistemas.chamados.entity.Prioridade;

import jakarta.validation.constraints.*;

public record ChamadoRequest(
    @NotBlank @Size(max = 150) String titulo,
    @NotBlank @Size(max = 2000) String descricao,
    @NotNull Prioridade prioridade,
    @NotNull Long clienteId,
    @NotNull Long categoriaId,
    Long tecnicoId
) {
}
