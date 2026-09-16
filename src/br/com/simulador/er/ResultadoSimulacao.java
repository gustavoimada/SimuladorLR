package br.com.simulador.er;

// guarda o resultado do teste de cada palavra
// assim a gente consegue mostrar bonitinho pro usuario se aceitou ou rejeitou
public class ResultadoSimulacao 
{
    // a palavra do jeito que o usuario digitou (pode ser "aba", "ε", ou vazia)
    private final String palavraOriginal;

    // a palavra real testada (se foi ε ou vazia, vira "" no java)
    private final String palavraTestada;

    // true se a palavra pertence a linguagem, false se nao pertence
    private final boolean aceita;

    // construtor simples
    public ResultadoSimulacao(String palavraOriginal, String palavraTestada, boolean aceita) 
    {
        this.palavraOriginal = palavraOriginal;
        this.palavraTestada = palavraTestada;
        this.aceita = aceita;
    }

    public String getPalavraOriginal() 
    {
        return palavraOriginal;
    }

    public String getPalavraTestada() 
    {
        return palavraTestada;
    }

    public boolean isAceita() 
    {
        return aceita;
    }

    // texto formatadinho com simbolo pra mostrar no resultado
    public String getStatusFormatado() 
    {
        return aceita ? "✓ ACEITA" : "✗ REJEITA";
    }

    @Override
    public String toString() 
    {
        // ex: "aba → ✓ ACEITA"
        String visual = palavraTestada.isEmpty() ? "E (vazia)" : palavraOriginal;
        return visual + " → " + getStatusFormatado();
    }
}
