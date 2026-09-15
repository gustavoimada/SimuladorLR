package br.com.simulador.af;

import java.util.Collections;
import java.util.List;

public class ResultadoAutomato {
    private final String palavraOriginal;
    private final String palavraTestada;
    private final boolean aceita;
    private final boolean automatoDeterministico;
    private final List<PassoAutomato> passos;

    public ResultadoAutomato(String palavraOriginal, String palavraTestada, boolean aceita, boolean automatoDeterministico, List<PassoAutomato> passos) {
        this.palavraOriginal = palavraOriginal;
        this.palavraTestada = palavraTestada;
        this.aceita = aceita;
        this.automatoDeterministico = automatoDeterministico;
        this.passos = passos;
    }

    public String getPalavraOriginal() {
        return palavraOriginal;
    }

    public String getPalavraTestada() {
        return palavraTestada;
    }

    public boolean isAceita() {
        return aceita;
    }

    public boolean isAutomatoDeterministico() {
        return automatoDeterministico;
    }

    public List<PassoAutomato> getPassos() {
        return Collections.unmodifiableList(passos);
    }

    public String getTipoAutomato() {
        return automatoDeterministico ? "AFD" : "AFND";
    }

    public String getStatusFormatado() {
        return aceita ? "ACEITA" : "REJEITA";
    }

    @Override
    public String toString() {
        String visual = palavraOriginal == null || palavraOriginal.isEmpty() ? "ε" : palavraOriginal;
        return visual + " -> " + getStatusFormatado() + " (" + getTipoAutomato() + ")";
    }
}
