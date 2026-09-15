package br.com.simulador.af;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class SimuladorAutomato {
    private final AutomatoFinito automato;

    public SimuladorAutomato(AutomatoFinito automato) {
        this.automato = automato;
    }

    public ResultadoAutomato reconhecer(String palavra) {
        Estado inicial = automato.getEstadoInicial();

        if(inicial == null) {
            throw new IllegalArgumentException("Defina um estado inicial antes de simular.");
        }

        String palavraTestada = normalizarPalavra(palavra);
        List<PassoAutomato> passos = new ArrayList<>();

        Set<Estado> atuais = new LinkedHashSet<>();
        atuais.add(inicial);
        atuais = fechamentoEpsilon(atuais);
        passos.add(new PassoAutomato(0, "", atuais, "inicio com fechamento-epsilon"));

        for(int i = 0; i < palavraTestada.length(); i++) {
            String simbolo = String.valueOf(palavraTestada.charAt(i));
            Set<Estado> aposMovimento = mover(atuais, simbolo);
            atuais = fechamentoEpsilon(aposMovimento);
            passos.add(new PassoAutomato(i + 1, simbolo, atuais, "leu '" + simbolo + "'"));
        }

        boolean aceita = contemEstadoFinal(atuais);

        return new ResultadoAutomato(palavra, palavraTestada, aceita, automato.isDeterministico(), passos);
    }

    public List<ResultadoAutomato> reconhecerMultiplas(String entradas) {
        List<ResultadoAutomato> resultados = new ArrayList<>();
        String texto = entradas == null ? "" : entradas;
        String[] linhas = texto.split("\\R", -1);

        for(String linha : linhas) {
            String palavra = linha.trim();
            if(!palavra.isEmpty()) {
                resultados.add(reconhecer(palavra));
            }
        }

        return resultados;
    }

    private Set<Estado> mover(Set<Estado> origem, String simbolo) {
        Set<Estado> destinos = new LinkedHashSet<>();

        for(Estado estado : origem) {
            for(Transicao transicao : automato.getTransicoesSaindoDe(estado)) {
                if(transicao.aceitaSimbolo(simbolo)) {
                    destinos.add(transicao.getDestino());
                }
            }
        }

        return destinos;
    }

    private Set<Estado> fechamentoEpsilon(Set<Estado> origem) {
        Set<Estado> fechamento = new LinkedHashSet<>(origem);
        Queue<Estado> fila = new ArrayDeque<>(origem);

        while(!fila.isEmpty()) {
            Estado estado = fila.remove();

            for(Transicao transicao : automato.getTransicoesSaindoDe(estado)) {
                if(transicao.possuiEpsilon() && !fechamento.contains(transicao.getDestino())) {
                    fechamento.add(transicao.getDestino());
                    fila.add(transicao.getDestino());
                }
            }
        }

        return fechamento;
    }

    private boolean contemEstadoFinal(Set<Estado> estados) {
        boolean encontrou = false;

        for(Estado estado : estados) {
            if(estado.isAceitacao()) {
                encontrou = true;
            }
        }

        return encontrou;
    }

    private String normalizarPalavra(String palavra) {
        String texto = palavra == null ? "" : palavra.trim();

        if(texto.equals(Transicao.EPSILON)) {
            texto = "";
        }

        return texto;
    }
}
