package br.simpliticket.sistemas.chamados.web.form;

import jakarta.validation.constraints.*;

public class CategoriaForm {

    @NotBlank @Size(max = 100)
    private String nome;

    @NotBlank @Size(max = 250)
    private String descricao;

    public String getNome() {return nome;}
    public void setNome(String nome) {this.nome = nome;}
    public String getDescricao() {return descricao;}
    public void setDescricao(String descricao) {this.descricao = descricao;}

}
