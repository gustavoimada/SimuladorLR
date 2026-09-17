package br.com.simulador.gr;

public class ProducaoRegular {
    private final String origem;
    private final String terminal;
    private final String destino;
    private final boolean vazia;

    public ProducaoRegular(String origem, String terminal, String destino, boolean vazia) {
        this.origem = origem;
        this.terminal = terminal;
        this.destino = destino;
        this.vazia = vazia;
    }

    public String getOrigem() {
        return origem;
    }

    public String getTerminal() {
        return terminal;
    }

    public String getDestino() {
        return destino;
    }

    public boolean isVazia() {
        return vazia;
    }

    public boolean encerraPalavra() {
        return !vazia && destino == null;
    }

    @Override
    public String toString() {
        String ladoDireito = vazia ? "E" : terminal + (destino == null ? "" : destino);
        return origem + " -> " + ladoDireito;
    }
}
