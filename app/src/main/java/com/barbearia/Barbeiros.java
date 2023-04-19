package com.barbearia;

public class Barbeiros {
    private int id;
    private String nome, foto, cidade;

    public Barbeiros(int id, String cidade, String nome, String foto) {
        this.id = id;
        this.cidade = cidade;
        this.nome = nome;
        this.foto = foto;
    }

    public Barbeiros() {

    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
