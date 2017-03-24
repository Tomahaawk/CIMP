package com.gohorse.calculadoraimpostoimportacao.core;

import java.security.Timestamp;

/**
 * Created by Lucas on 24/03/2017.
 */

public class Moeda {

    private String nome;
    private float valor;
    private Timestamp ultimaConsulta;
    private String fonte;

    public Moeda(String nome, float valor, Timestamp ultimaConsulta, String fonte) {
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

    public void setUltimaConsulta(Timestamp ultimaConsulta) {
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

    public Timestamp getUltimaConsulta() {
        return ultimaConsulta;
    }

    public String getFonte() {
        return fonte;
    }
}
