package br.com.simulador.af;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class PassoAutomato {
    private final int indice;
    private final String simboloConsumido;
    private final Set<String> estadosAtivos;
    private final String descricao;

    public PassoAutomato(int indice, String simboloConsumido, Set<Estado> estadosAtivos, String descricao) {
        this.indice = indice;
        this.simboloConsumido = simboloConsumido;
        this.estadosAtivos = new LinkedHashSet<>();
        this.descricao = descricao;

        for(Estado estado : estadosAtivos) {
            this.estadosAtivos.add(estado.getNome());
        }
    }

    public int getIndice() {
        return indice;
    }

    public String getSimboloConsumido() {
        return simboloConsumido;
    }

    public Set<String> getEstadosAtivos() {
        return Collections.unmodifiableSet(estadosAtivos);
    }

    public String getDescricao() {
        return descricao;
    }

    public String getEstadosFormatados() {
        String resultado = estadosAtivos.isEmpty() ? "{}" : estadosAtivos.toString();
        return resultado;
    }

    @Override
    public String toString() {
        return "Passo " + indice + " | " + descricao + " | ativos: " + getEstadosFormatados();
    }
}
