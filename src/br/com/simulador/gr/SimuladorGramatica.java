package br.com.simulador.gr;

import java.util.LinkedHashSet;
import java.util.Set;

public class SimuladorGramatica {
    private final GramaticaRegular gramatica;

    public SimuladorGramatica(String textoGramatica) {
        this.gramatica = AnalisadorGramatica.analisar(textoGramatica);
    }

    public GramaticaRegular getGramatica() {
        return gramatica;
    }

    public ResultadoGramatica reconhecer(String palavra) {
        String original = palavra == null ? "" : palavra.trim();
        String testada = original;
        if(testada.equals("E") || testada.equals("ε")) {
            testada = "";
        }

        Set<String> estadosAtuais = new LinkedHashSet<>();
        estadosAtuais.add(gramatica.getSimboloInicial());
        boolean aceita = false;

        for(int i = 0; i < testada.length(); i++) {
            String simbolo = String.valueOf(testada.charAt(i));
            Set<String> proximos = new LinkedHashSet<>();
            boolean ultimoSimbolo = i == testada.length() - 1;

            for(String estado : estadosAtuais) {
                for(ProducaoRegular producao : gramatica.getProducoesDe(estado)) {
                    if(!producao.isVazia() && producao.getTerminal().equals(simbolo)) {
                        if(producao.encerraPalavra() && ultimoSimbolo) {
                            aceita = true;
                        } else if(producao.getDestino() != null) {
                            proximos.add(producao.getDestino());
                        }
                    }
                }
            }

            estadosAtuais = proximos;
        }

        if(testada.isEmpty() || !aceita) {
            for(String estado : estadosAtuais) {
                if(possuiProducaoVazia(estado)) {
                    aceita = true;
                }
            }
        }

        return new ResultadoGramatica(original, testada, aceita);
    }

    private boolean possuiProducaoVazia(String naoTerminal) {
        boolean encontrou = false;

        for(ProducaoRegular producao : gramatica.getProducoesDe(naoTerminal)) {
            if(producao.isVazia()) {
                encontrou = true;
            }
        }

        return encontrou;
    }
}
