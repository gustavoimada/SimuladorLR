package br.com.simulador.gr;

import java.util.ArrayList;
import java.util.List;

public class GramaticaRegular {
    private final String textoOriginal;
    private final String simboloInicial;
    private final List<String> naoTerminais;
    private final List<String> terminais;
    private final List<ProducaoRegular> producoes;

    public GramaticaRegular(
        String textoOriginal,
        String simboloInicial,
        List<String> naoTerminais,
        List<String> terminais,
        List<ProducaoRegular> producoes
    ) {
        this.textoOriginal = textoOriginal;
        this.simboloInicial = simboloInicial;
        this.naoTerminais = naoTerminais;
        this.terminais = terminais;
        this.producoes = producoes;
    }

    public String getTextoOriginal() {
        return textoOriginal;
    }

    public String getSimboloInicial() {
        return simboloInicial;
    }

    public List<String> getNaoTerminais() {
        return naoTerminais;
    }

    public List<String> getTerminais() {
        return terminais;
    }

    public List<ProducaoRegular> getProducoes() {
        return producoes;
    }

    public List<ProducaoRegular> getProducoesDe(String naoTerminal) {
        List<ProducaoRegular> encontradas = new ArrayList<>();

        for(ProducaoRegular producao : producoes) {
            if(producao.getOrigem().equals(naoTerminal)) {
                encontradas.add(producao);
            }
        }

        return encontradas;
    }
}
