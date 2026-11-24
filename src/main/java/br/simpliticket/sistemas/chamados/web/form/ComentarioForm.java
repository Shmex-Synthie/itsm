package br.simpliticket.sistemas.chamados.web.form;

import jakarta.validation.constraints.*;

public class ComentarioForm {

    @NotBlank @Size(max = 2000)
    private String texto;

    @NotBlank @Size(max = 120)
    private String autor;

    public String getTexto() {return texto;}
    public void setTexto(String texto) {this.texto = texto;}
    public String getAutor() {return autor;}
    public void setAutor(String autor) {this.autor = autor;}

}
