package br.simpliticket.sistemas.chamados.web.form;

import jakarta.validation.constraints.*;

public class ClienteForm {

    @NotBlank @Size(max = 100)
    private String nome;

    @NotBlank @Email @Size(max = 120)
    private String email;

    @Size(max = 20)
    private String telefone;

    public String getNome() {return nome;}
    public void setNome(String nome) {this.nome = nome;}
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    public String getTelefone() {return telefone;}
    public void setTelefone(String telefone) {this.telefone = telefone;}

}
