package br.com.simulador;

import br.com.simulador.er.ResultadoSimulacao;
import br.com.simulador.er.SimuladorER;

import java.util.Scanner;

public class Main 
{
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) 
    {
        boolean rodando = true;

        while(rodando)
        {
            System.out.println("\n=========================================================");
            System.out.println("       SIMULADOR DE LINGUAGENS REGULARES - LFA           ");
            System.out.println("      Representacao 1: Expressoes Regulares (ER)         ");
            System.out.println("=========================================================");
            System.out.println("  1 - Digitar Expressao Regular e testar palavra(s)");
            System.out.println("  0 - Sair");
            System.out.println("=========================================================");
            System.out.print("Escolha uma opcao: ");

            String opcao = scanner.nextLine().trim();

            switch(opcao) 
            {
                case "1":
                    executarSimulacaoManual();
                    break;

                case "0":
                    rodando = false;
                    System.out.println("\nValeu! Encerrando o simulador.");
                    break;

                default:
                    System.out.println("\n[!] Opcao invalida, tenta de novo.");
                    System.out.print("\nPressione [Enter] para continuar...");
                    scanner.nextLine();
                    break;
            }
        }
    }

    // logica de simulacao da er informada pelo usuario
    private static void executarSimulacaoManual() 
    {
        System.out.println("\n--- SIMULACAO DA ER ---");
        System.out.println("Regras da ER: use letras, numeros, '.' (concat), '|' ou '+' (uniao), '*' (kleene) e '()'");
        System.out.println("Letras e numeros seguidos dispensam o ponto (ex: aa). Com grupos ou apos '*', use '.' (ex: (aa+b)*.abb.a*).");
        System.out.print("Digite a Expressao Regular: ");
        String er = scanner.nextLine().trim();

        SimuladorER simulador = null;

        try 
        {
            // valida e compila a expressao
            simulador = new SimuladorER(er);
        } 
        catch(IllegalArgumentException e) 
        {
            System.out.println("\n[X] ER INVALIDA: " + e.getMessage());
            System.out.print("\nPressione [Enter] para continuar...");
            scanner.nextLine();
        }

        // se a er foi validada sem erros, abre o teste de palavras
        if(simulador != null) 
        {
            System.out.println("\n[OK] Expressao regular valida!");
            System.out.println("---------------------------------------------------------");
            System.out.println("ER informada: " + simulador.getExpressaoRegular().getExpressaoOriginal());
            System.out.println("Regex Java:   " + simulador.getExpressaoRegular().getRegexJava());
            System.out.println("---------------------------------------------------------");

            System.out.println("Digite as palavras para testar.");
            System.out.println("(Para testar palavra vazia digite \u03b5 ou apenas aperte Enter)");
            System.out.println("(Digite 'FIM' para voltar ao menu)\n");

            int total = 0;
            int aceitas = 0;
            int rejeitadas = 0;

            boolean testandoPalavras = true;

            while(testandoPalavras) 
            {
                System.out.print("Palavra: ");
                String entrada = scanner.nextLine();

                if(entrada.trim().equalsIgnoreCase("FIM")) 
                {
                    testandoPalavras = false;
                } 
                else 
                {
                    ResultadoSimulacao resultado = simulador.simularPalavra(entrada);
                    total++;

                    if(resultado.isAceita()) 
                    {
                        aceitas++;
                        System.out.println("  -> " + resultado.toString() + " (Pertence a linguagem)");
                    } 
                    else 
                    {
                        rejeitadas++;
                        System.out.println("  -> " + resultado.toString() + " (Nao pertence a linguagem)");
                    }
                }
            }

            if(total>0) 
            {
                System.out.println("\nResumo: " + total + " palavra(s) testada(s) | " + aceitas + " aceita(s) | " + rejeitadas + " rejeitada(s)");
            }

            System.out.print("\nPressione [Enter] para continuar...");
            scanner.nextLine();
        }
    }
}
