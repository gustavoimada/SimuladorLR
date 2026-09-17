package br.com.simulador.gr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GramaticaRegular {
    private final String textoOriginal;
    private final String simboloInicial;
    private final Set<String> naoTerminais;
    private final Set<String> terminais;
    private final List<ProducaoRegular> producoes;

    public GramaticaRegular(
        String textoOriginal,
        String simboloInicial,
        Set<String> naoTerminais,
        Set<String> terminais,
        List<ProducaoRegular> producoes
    ) {
        this.textoOriginal = textoOriginal;
        this.simboloInicial = simboloInicial;
        this.naoTerminais = new LinkedHashSet<>(naoTerminais);
        this.terminais = new LinkedHashSet<>(terminais);
        this.producoes = new ArrayList<>(producoes);
    }

    public String getTextoOriginal() {
        return textoOriginal;
    }

    public String getSimboloInicial() {
        return simboloInicial;
    }

    public Set<String> getNaoTerminais() {
        return Collections.unmodifiableSet(naoTerminais);
    }

    public Set<String> getTerminais() {
        return Collections.unmodifiableSet(terminais);
    }

    public List<ProducaoRegular> getProducoes() {
        return Collections.unmodifiableList(producoes);
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
