package br.com.simulador.af;

import java.util.LinkedHashSet;
import java.util.Set;

public class Transicao {
    public static final String EPSILON = "ε";

    private Estado origem;
    private Estado destino;
    private String rotulo;

    public Transicao(Estado origem, Estado destino, String rotulo) {
        this.origem = origem;
        this.destino = destino;
        setRotulo(rotulo);
    }

    public Estado getOrigem() {
        return origem;
    }

    public void setOrigem(Estado origem) {
        this.origem = origem;
    }

    public Estado getDestino() {
        return destino;
    }

    public void setDestino(Estado destino) {
        this.destino = destino;
    }

    public String getRotulo() {
        return rotulo;
    }

    public void setRotulo(String rotulo) {
        String texto = rotulo == null ? "" : rotulo.trim();
        if(texto.isEmpty()) {
            texto = EPSILON;
        }
        this.rotulo = texto;
    }

    public Set<String> getSimbolos() {
        Set<String> simbolos = new LinkedHashSet<>();
        String[] partes = rotulo.split(",");

        for(String parte : partes) {
            String simbolo = normalizarSimbolo(parte);
            if(!simbolo.isEmpty()) {
                simbolos.add(simbolo);
            }
        }

        if(simbolos.isEmpty()) {
            simbolos.add(EPSILON);
        }

        return simbolos;
    }

    public boolean possuiEpsilon() {
        return getSimbolos().contains(EPSILON);
    }

    public boolean aceitaSimbolo(String simbolo) {
        return getSimbolos().contains(simbolo);
    }

    public static String normalizarSimbolo(String simbolo) {
        String valor = simbolo == null ? "" : simbolo.trim();

        if(valor.equalsIgnoreCase("epsilon") || valor.equalsIgnoreCase("eps")) {
            valor = EPSILON;
        }

        return valor;
    }

    @Override
    public String toString() {
        return origem.getNome() + " --" + rotulo + "--> " + destino.getNome();
    }
}
