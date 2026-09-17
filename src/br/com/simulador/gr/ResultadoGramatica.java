package br.com.simulador.gr;

public class ResultadoGramatica {
    private final String palavraOriginal;
    private final String palavraTestada;
    private final boolean aceita;

    public ResultadoGramatica(String palavraOriginal, String palavraTestada, boolean aceita) {
        this.palavraOriginal = palavraOriginal;
        this.palavraTestada = palavraTestada;
        this.aceita = aceita;
    }

    public boolean isAceita() {
        return aceita;
    }

    public String getStatusFormatado() {
        return aceita ? "ACEITA" : "REJEITA";
    }

    @Override
    public String toString() {
        String visual = palavraTestada.isEmpty() ? "E (vazia)" : palavraOriginal;
        return visual + " -> " + getStatusFormatado();
    }
}
