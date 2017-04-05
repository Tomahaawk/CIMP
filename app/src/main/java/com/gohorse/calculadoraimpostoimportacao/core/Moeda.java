package com.gohorse.calculadoraimpostoimportacao.core;


public class Moeda {

    private String nome;
    private float valor;
    private String ultimaConsulta;
    private String fonte;

    public Moeda() {
    }

    public Moeda(String nome, float valor, String ultimaConsulta, String fonte) {
        this.nome = nome;
        this.valor = valor;
        this.ultimaConsulta = ultimaConsulta;
        this.fonte = fonte;
    }


    //SETTERS
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setValor(float valor) {

        this.valor = valor;
    }

    public void setUltimaConsulta(String ultimaConsulta) {
        this.ultimaConsulta = ultimaConsulta;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    //GETTERS
    public String getNome() {
        return nome;
    }

    public float getValor() {

        return valor;
    }

    public String getUltimaConsulta() {
        return ultimaConsulta;
    }

    public String getFonte() {
        return fonte;
    }
}
