package br.com.simulador.er;

import java.util.ArrayList;
import java.util.List;


// pega a er do usuario, manda validar, manda converter e testa as palavras com .matches()
public class SimuladorER 
{
    private final ExpressaoRegular expressaoRegular;

    // construtor simples que valida e converte a expressao
    public SimuladorER(String erOriginal) 
    {
        String limpa;

        // se o usuario n digitou nada na hora de colocar a expressao
        if(erOriginal == null)
            limpa = "";
        else
            limpa = erOriginal.replaceAll("\\s+", "").replace('ε', 'E'); // normaliza a palavra vazia

        // mandamos a versao ja limpa pro validador, que so precisa checar a sintaxe
        ValidadorER.validar(limpa);

        // convertemos a er que a gente aprendeu em LFA pra sintaxe do regex java
        String regexJava = ConversorRegex.converter(limpa);

        // criamos nosso objeto que guarda essas informacoes
        this.expressaoRegular = new ExpressaoRegular(erOriginal, limpa, regexJava);
    }

    // testa uma unica palavra usando o metodo nativo matches da propria String
    public ResultadoSimulacao simularPalavra(String palavra) 
    {
        String palavraTratada = palavra == null ? "" : palavra.trim();
        if(palavraTratada.equals("E") || palavraTratada.equals("ε"))
            palavraTratada = "";

        // aqui usamos direto o metodo matches da String do java, bem simples de entender
        boolean aceita = palavraTratada.matches(this.expressaoRegular.getRegexJava());

        return new ResultadoSimulacao(palavra, palavraTratada, aceita);
    }

    public ExpressaoRegular getExpressaoRegular() 
    {
        return expressaoRegular;
    }
}
