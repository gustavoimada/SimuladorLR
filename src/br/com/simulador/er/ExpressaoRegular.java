package br.com.simulador.er;

// classe basica que representa a expressao regular do usuario
// ela guarda a er original q o usuario digitou e a versao convertida pro regex java
public class ExpressaoRegular 
{
    // texto original q o cara digitou na tela
    private final String expressaoOriginal;
    
    // expressao limpa (sem espacos) pra facilitar a vida na hora de validar
    private final String expressaoLimpa;
    
    // aqui fica o regex final compilavel pelo java (tipo ^(a|b)$)
    private final String regexJava;

    // construtor simples pra inicializar os campos
    public ExpressaoRegular(String expressaoOriginal, String expressaoLimpa, String regexJava) 
    {
        this.expressaoOriginal = expressaoOriginal;
        this.expressaoLimpa = expressaoLimpa;
        this.regexJava = regexJava;
    }

    // pega a expressao como o usuario digitou
    public String getExpressaoOriginal() 
    {
        return expressaoOriginal;
    }

    // pega a expressao sem espacos em branco
    public String getExpressaoLimpa() 
    {
        return expressaoLimpa;
    }

    // pega a regex pronta pro java util regex
    public String getRegexJava() 
    {
        return regexJava;
    }

    @Override
    public String toString() 
    {
        return "ER Original: " + expressaoOriginal + " | Regex Java: " + regexJava;
    }
}
