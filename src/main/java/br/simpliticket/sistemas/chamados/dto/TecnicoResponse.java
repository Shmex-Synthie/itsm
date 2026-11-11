package br.simpliticket.sistemas.chamados.dto;

import br.simpliticket.sistemas.chamados.entity.Tecnico;
import br.simpliticket.sistemas.chamados.entity.Perfil;

public record TecnicoResponse(Long id, String nome, String email, Perfil perfil) {
    public static TecnicoResponse de(Tecnico t) {
        return new TecnicoResponse(t.getId(), t.getNome(), t.getEmail(), t.getPerfil());
    }

}
