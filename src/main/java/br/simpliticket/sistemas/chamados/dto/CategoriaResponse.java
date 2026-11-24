package br.simpliticket.sistemas.chamados.dto;

import br.simpliticket.sistemas.chamados.entity.Categoria;

public record CategoriaResponse(Long id, String nome, String descricao){
    public static CategoriaResponse de(Categoria c){
        return new CategoriaResponse(c.getId(), c.getNome(), c.getDescricao());
    }
}