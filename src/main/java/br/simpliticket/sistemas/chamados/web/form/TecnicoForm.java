package br.simpliticket.sistemas.chamados.web.form;

import jakarta.validation.constraints.*;
import br.simpliticket.sistemas.chamados.entity.*;

public class TecnicoForm {

    @NotBlank @Size(max = 100)
    private String nome;

    @NotBlank @Email @Size(max = 120)
    private String email;

    @NotNull
    private Perfil perfil;

    public String getNome() {return nome;}
    public void setNome(String nome) {this.nome = nome;}
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    public Perfil getPerfil() {return perfil;}
    public void setPerfil(Perfil perfil) {this.perfil = perfil;}
    
}
