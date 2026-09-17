package br.com.simulador.gr;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AnalisadorGramatica {
    private AnalisadorGramatica() {
    }

    public static GramaticaRegular analisar(String textoOriginal) {
        String texto = textoOriginal == null ? "" : textoOriginal.trim();
        if(texto.isEmpty()) {
            throw new IllegalArgumentException("A gramatica regular nao pode ficar vazia.");
        }

        String[] linhas = texto.split("\\R");
        Set<String> naoTerminais = coletarNaoTerminais(linhas);
        List<ProducaoRegular> producoes = new ArrayList<>();
        Set<String> terminais = new LinkedHashSet<>();
        String simboloInicial = null;

        for(int i = 0; i < linhas.length; i++) {
            String linha = linhas[i].trim();
            if(!linha.isEmpty()) {
                String[] partes = separarProducao(linha, i + 1);
                String origem = partes[0].trim();
                if(simboloInicial == null) {
                    simboloInicial = origem;
                }

                String[] alternativas = partes[1].split("\\|", -1);
                for(String alternativa : alternativas) {
                    ProducaoRegular producao = analisarAlternativa(origem, alternativa, naoTerminais, i + 1);
                    producoes.add(producao);
                    if(!producao.isVazia()) {
                        terminais.add(producao.getTerminal());
                    }
                }
            }
        }

        validarReferencias(producoes, naoTerminais);
        return new GramaticaRegular(textoOriginal, simboloInicial, naoTerminais, terminais, producoes);
    }

    private static Set<String> coletarNaoTerminais(String[] linhas) {
        Set<String> naoTerminais = new LinkedHashSet<>();

        for(int i = 0; i < linhas.length; i++) {
            String linha = linhas[i].trim();
            if(!linha.isEmpty()) {
                String[] partes = separarProducao(linha, i + 1);
                String origem = partes[0].trim();
                validarNaoTerminal(origem, i + 1);
                naoTerminais.add(origem);
            }
        }

        return naoTerminais;
    }

    private static String[] separarProducao(String linha, int numeroLinha) {
        String normalizada = linha.replace("→", "->");
        String[] partes = normalizada.split("->", -1);

        if(partes.length != 2) {
            throw new IllegalArgumentException("Linha " + numeroLinha + ": use o formato S -> aA | b.");
        }

        if(partes[0].trim().isEmpty() || partes[1].trim().isEmpty()) {
            throw new IllegalArgumentException("Linha " + numeroLinha + ": a producao esta incompleta.");
        }

        return partes;
    }

    private static void validarNaoTerminal(String simbolo, int numeroLinha) {
        if(simbolo.length() != 1 || !Character.isUpperCase(simbolo.charAt(0))) {
            throw new IllegalArgumentException("Linha " + numeroLinha + ": o lado esquerdo deve ser uma letra maiuscula.");
        }
    }

    private static ProducaoRegular analisarAlternativa(
        String origem,
        String alternativaOriginal,
        Set<String> naoTerminais,
        int numeroLinha
    ) {
        String alternativa = alternativaOriginal.replaceAll("\\s+", "");
        if(alternativa.isEmpty()) {
            throw new IllegalArgumentException("Linha " + numeroLinha + ": existe uma alternativa vazia apos '|'.");
        }

        if(alternativa.equals("E") || alternativa.equals("ε")) {
            return new ProducaoRegular(origem, "", null, true);
        }

        if(alternativa.length() < 1 || alternativa.length() > 2) {
            throw new IllegalArgumentException(
                "Linha " + numeroLinha + ": '" + alternativa + "' deve ser a, aB ou E."
            );
        }

        char terminal = alternativa.charAt(0);
        if(!isTerminalValido(terminal)) {
            throw new IllegalArgumentException(
                "Linha " + numeroLinha + ": '" + terminal + "' nao e um terminal valido."
            );
        }

        String destino = null;
        if(alternativa.length() == 2) {
            destino = String.valueOf(alternativa.charAt(1));
            if(!naoTerminais.contains(destino)) {
                throw new IllegalArgumentException(
                    "Linha " + numeroLinha + ": o nao terminal " + destino + " nao possui producao declarada."
                );
            }
        }

        return new ProducaoRegular(origem, String.valueOf(terminal), destino, false);
    }

    private static boolean isTerminalValido(char simbolo) {
        return !Character.isWhitespace(simbolo)
            && !Character.isUpperCase(simbolo)
            && simbolo != '|'
            && simbolo != '-'
            && simbolo != '>'
            && simbolo != 'ε';
    }

    private static void validarReferencias(List<ProducaoRegular> producoes, Set<String> naoTerminais) {
        for(String naoTerminal : naoTerminais) {
            boolean possuiProducao = false;
            for(ProducaoRegular producao : producoes) {
                if(producao.getOrigem().equals(naoTerminal)) {
                    possuiProducao = true;
                }
            }

            if(!possuiProducao) {
                throw new IllegalArgumentException("O nao terminal " + naoTerminal + " nao possui producoes.");
            }
        }
    }
}
