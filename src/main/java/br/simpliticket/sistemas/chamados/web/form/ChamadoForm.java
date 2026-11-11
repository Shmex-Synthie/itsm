package br.simpliticket.sistemas.chamados.web.form;

import jakarta.validation.constraints.*;
import br.simpliticket.sistemas.chamados.entity.*;

public class ChamadoForm {
    
    @NotBlank @Size(max = 150)
    private String titulo;

    @NotBlank @Size(max = 2000)
    private String descricao;

    @NotNull
    private Prioridade prioridade;

    @NotNull
    private Long clienteId;

    @NotNull
    private Long categoriaId;

    
    private Long tecnicoId;
    public String getTitulo() {return titulo;}
    public void setTitulo(String titulo) {this.titulo = titulo;}
    public String getDescricao() {return descricao;}
    public void setDescricao(String descricao) {this.descricao = descricao;}
    public Prioridade getPrioridade() {return prioridade;}
    public void setPrioridade(Prioridade prioridade) {this.prioridade = prioridade;}
    public Long getClienteId() {return clienteId;}
    public void setClienteId(Long clienteId) {this.clienteId = clienteId;}
    public Long getCategoriaId() {return categoriaId;}
    public void setCategoriaId(Long categoriaId) {this.categoriaId = categoriaId;}
    public Long getTecnicoId() {return tecnicoId;}
    public void setTecnicoId(Long tecnicoId) {this.tecnicoId = tecnicoId;}

}
