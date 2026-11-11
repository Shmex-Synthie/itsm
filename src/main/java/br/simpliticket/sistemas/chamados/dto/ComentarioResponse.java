package br.simpliticket.sistemas.chamados.dto;

import java.time.LocalDateTime;
import br.simpliticket.sistemas.chamados.entity.Comentario;
public record ComentarioResponse(Long id, String texto, LocalDateTime data, String autor, Long chamadoId) {
    public static ComentarioResponse de(Comentario c) {
        return new ComentarioResponse(c.getId(), c.getTexto(), c.getData(), c.getAutor(), c.getChamado().getId());
    }

}
