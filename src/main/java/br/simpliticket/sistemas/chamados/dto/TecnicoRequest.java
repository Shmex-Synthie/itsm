package br.simpliticket.sistemas.chamados.dto;

import br.simpliticket.sistemas.chamados.entity.Perfil;

import jakarta.validation.constraints.*;

public record TecnicoRequest(
    @NotBlank @Size(max = 100) String nome,
    @NotBlank @Email @Size(max = 120) String email,
    @NotNull Perfil perfil
) {
}
