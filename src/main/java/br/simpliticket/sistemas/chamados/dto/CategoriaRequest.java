package br.simpliticket.sistemas.chamados.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequest(
    @NotBlank(message="O nome da categoria é obrigatório.")
    @Size(max=100, message="O nome da categoria deve ter no máximo 100 caracteres.")
    String nome,

    @NotBlank(message="A descrição da categoria não pode ficar vazia.")
    @Size(message="A descrição da categoria deve ter no máximo 250 caracteres.")
    String descricao
) {}