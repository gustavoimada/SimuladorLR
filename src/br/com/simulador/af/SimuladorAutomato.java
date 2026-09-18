package br.com.simulador.af;

import java.util.ArrayList;
import java.util.List;

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

        List<Estado> atuais = new ArrayList<>();
        atuais.add(inicial);
        atuais = fechamentoEpsilon(atuais);
        passos.add(new PassoAutomato(0, "", atuais, "inicio com fechamento-epsilon"));

        for(int i = 0; i < palavraTestada.length(); i++) {
            String simbolo = String.valueOf(palavraTestada.charAt(i));
            List<Estado> aposMovimento = mover(atuais, simbolo);
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

    private List<Estado> mover(List<Estado> origem, String simbolo) {
        List<Estado> destinos = new ArrayList<>();

        for(Estado estado : origem) {
            for(Transicao transicao : automato.getTransicoesSaindoDe(estado)) {
                if(transicao.aceitaSimbolo(simbolo) && !contemEstado(destinos, transicao.getDestino())) {
                    destinos.add(transicao.getDestino());
                }
            }
        }

        return destinos;
    }

    private List<Estado> fechamentoEpsilon(List<Estado> origem) {
        List<Estado> fechamento = new ArrayList<>();

        for(Estado estado : origem) {
            if(!contemEstado(fechamento, estado)) {
                fechamento.add(estado);
            }
        }

        int indice = 0;
        while(indice < fechamento.size()) {
            Estado estado = fechamento.get(indice);

            for(Transicao transicao : automato.getTransicoesSaindoDe(estado)) {
                if(transicao.possuiEpsilon() && !contemEstado(fechamento, transicao.getDestino())) {
                    fechamento.add(transicao.getDestino());
                }
            }

            indice++;
        }

        return fechamento;
    }

    private boolean contemEstadoFinal(List<Estado> estados) {
        boolean encontrou = false;

        for(Estado estado : estados) {
            if(estado.isAceitacao()) {
                encontrou = true;
            }
        }

        return encontrou;
    }

    private boolean contemEstado(List<Estado> estados, Estado procurado) {
        boolean encontrou = false;

        for(Estado estado : estados) {
            if(estado == procurado) {
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
