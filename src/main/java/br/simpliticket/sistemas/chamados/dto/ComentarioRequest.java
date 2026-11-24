package br.simpliticket.sistemas.chamados.dto;

import jakarta.validation.constraints.*;
public record ComentarioRequest(@NotBlank @Size(max = 2000) String texto,
    @NotBlank @Size(max = 120) String autor)
{}
