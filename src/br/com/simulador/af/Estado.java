package br.com.simulador.af;

public class Estado {
    private String nome;
    private double x;
    private double y;
    private boolean inicial;
    private boolean aceitacao;

    public Estado(String nome, double x, double y) {
        this.nome = nome;
        this.x = x;
        this.y = y;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean isInicial() {
        return inicial;
    }

    public void setInicial(boolean inicial) {
        this.inicial = inicial;
    }

    public boolean isAceitacao() {
        return aceitacao;
    }

    public void setAceitacao(boolean aceitacao) {
        this.aceitacao = aceitacao;
    }

    @Override
    public String toString() {
        String marcadorInicial = inicial ? "-> " : "";
        String marcadorFinal = aceitacao ? " *" : "";
        return marcadorInicial + nome + marcadorFinal;
    }
}
