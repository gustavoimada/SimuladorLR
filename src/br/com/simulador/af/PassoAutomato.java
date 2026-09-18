package br.com.simulador.af;

import java.util.ArrayList;
import java.util.List;

public class PassoAutomato {
    private final int indice;
    private final String simboloConsumido;
    private final List<String> estadosAtivos;
    private final String descricao;

    public PassoAutomato(int indice, String simboloConsumido, List<Estado> estadosAtivos, String descricao) {
        this.indice = indice;
        this.simboloConsumido = simboloConsumido;
        this.estadosAtivos = new ArrayList<>();
        this.descricao = descricao;

        for(Estado estado : estadosAtivos) {
            if(!this.estadosAtivos.contains(estado.getNome())) {
                this.estadosAtivos.add(estado.getNome());
            }
        }
    }

    public int getIndice() {
        return indice;
    }

    public String getSimboloConsumido() {
        return simboloConsumido;
    }

    public List<String> getEstadosAtivos() {
        return estadosAtivos;
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
