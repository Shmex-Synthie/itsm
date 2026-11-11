package br.simpliticket.sistemas.chamados.dto;

import br.simpliticket.sistemas.chamados.entity.StatusChamado;

import jakarta.validation.constraints.NotNull;
public record StatusChamadoRequest(@NotNull StatusChamado status) {
}
