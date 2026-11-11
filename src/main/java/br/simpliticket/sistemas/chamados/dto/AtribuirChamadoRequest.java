package br.simpliticket.sistemas.chamados.dto;

import jakarta.validation.constraints.NotNull;
public record AtribuirChamadoRequest(@NotNull Long tecnicoId) {
}
