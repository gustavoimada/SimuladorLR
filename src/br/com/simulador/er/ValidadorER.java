package br.com.simulador.er;

// aqui fica toda a validacao da expressao regular
// a gente garante que o usuario so use o que o professor permitiu na disciplina
// codigo puramente estruturado: sem returns antecipados, breaks ou continues em ifs crus
public class ValidadorER 
{
    // valida a er — agora ela ja chega limpa (sem espacos) pois quem limpa eh o SimuladorER
    public static void validar(String er) 
    {
        // checa se nao veio nulo ou so espaco vazio
        if(er == null || er.trim().isEmpty()) 
        {
            throw new IllegalArgumentException("a expressao regular nao pode ser vazia.");
        }

        // checar se tem algum caractere estranho que nao esteja na lista dos permitidos
        for(int i = 0; i < er.length(); i++) 
        {
            char c = er.charAt(i);
            if(!isSimboloPermitido(c)) 
            {
                throw new IllegalArgumentException("caractere invalido '" + c + "'");
            }
        }

        // verifica se os parenteses estao balanceados
        validarParenteses(er);

        // verifica se os operadores estao em lugares validos (sem operador no inicio/fim, sem ** etc)
        validarOperadores(er);
    }

    // metodo auxiliar estruturado com retorno unico no final
    private static boolean isSimboloPermitido(char c) 
    {
        boolean ehLetra = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
        boolean ehDigito = (c >= '0' && c <= '9');
        boolean ehOperador = (c == '|' || c == '+' || c == '.' || c == '*' || c == '(' || c == ')');

        return ehLetra || ehDigito || ehOperador;
    }

    // valida o abre e fecha de parenteses pra nao sobrar nenhum aberto ou fechar sem abrir
    private static void validarParenteses(String s) 
    {
        int contador = 0;
        for(int i = 0; i < s.length(); i++) 
        {
            char c = s.charAt(i);
            if(c == '(') 
            {
                contador++;
                // se logo depois de '(' ja fechar ')', tipo '()', ta errado pq ta vazio
                if(i + 1 < s.length() && s.charAt(i + 1) == ')') 
                {
                    throw new IllegalArgumentException("parenteses vazios '()' nao sao permitidos na er.");
                }
            } 
            else 
                if(c == ')') 
                {
                    contador--;
                    // se o contador ficou negativo quer dizer que fechou parentese antes de abrir
                    if(contador < 0) 
                    {
                        throw new IllegalArgumentException("parenteses desbalanceados: tem ')' fechando sem ter sido aberto antes.");
                    }
                }
        }

        // se no final o contador nao zerou, ficou parentese aberto sem fechar
        if(contador > 0) 
        {
            throw new IllegalArgumentException("parenteses desbalanceados: tem '(' aberto que nao foi fechado.");
        }
    }

    // checa se eh operando (letra ou numero) com retorno unico
    private static boolean isOperando(char c)
    {
        boolean ehLetra = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
        boolean ehDigito = (c >= '0' && c <= '9');

        return ehLetra || ehDigito;
    }

    // checa se eh operador binario (uniao | e +, concatenacao .)
    private static boolean isOperadorBinario(char c)
    {
        return c == '|' || c == '+' || c == '.';
    }

    // aqui a gente faz a varredura das posicoes dos operadores pra pegar erros sintaticos
    private static void validarOperadores(String s) 
    {
        int n = s.length();

        // er nao pode comecar com operador binario nem com *
        char primeiro = s.charAt(0);
        if(isOperadorBinario(primeiro))
        {
            throw new IllegalArgumentException("a expressao nao pode comecar com o operador '" + primeiro + "'.");
        }
        if(primeiro == '*') 
        {
            throw new IllegalArgumentException("fechamento de kleene '*' nao pode ficar no inicio da expressao.");
        }

        // er nao pode terminar com operador binario
        char ultimo = s.charAt(n - 1);
        if(isOperadorBinario(ultimo)) 
        {
            throw new IllegalArgumentException("a expressao nao pode terminar com o operador '" + ultimo + "'.");
        }

        // agora varre elemento por elemento comparando com o proximo
        for(int i = 0; i < n; i++) 
        {
            char atual = s.charAt(i);
            char prox = (i + 1 < n) ? s.charAt(i + 1) : '\0';

            // caso 1: operador binario (| ou + ou .)
            if(isOperadorBinario(atual)) 
            {
                // nao pode ter outro operador binario logo em seguida, tipo '||', '++', '..', '|+'
                if(isOperadorBinario(prox)) 
                {
                    throw new IllegalArgumentException("operadores consecutivos invalidos: '" + atual + "" + prox + "'.");
                }
                // nao pode ter '*' logo depois de operador binario, tipo '|*' ou '.*'
                if(prox == '*') 
                {
                    throw new IllegalArgumentException("o operador '*' nao pode vir logo apos o operador '" + atual + "'.");
                }
                // nao pode ter ')' logo depois de operador binario, tipo '(a|)'
                if(prox == ')') 
                {
                    throw new IllegalArgumentException("o operador '" + atual + "' nao pode ficar antes de fechar parenteses ')'.");
                }
            }

            // caso 2: fechamento de kleene '*'
            if(atual == '*') 
            {
                // nao pode dois asteriscos seguidos, tipo a**
                if(prox == '*') 
                {
                    throw new IllegalArgumentException("fechamento de kleene duplicado '**' nao eh permitido.");
                }
            }

            // caso 3: abre parenteses '('
            if(atual == '(') 
            {
                // nao pode ter operador binario logo apos abrir parenteses, tipo '(|a)' ou '(.b)'
                if(isOperadorBinario(prox)) 
                {
                    throw new IllegalArgumentException("o operador '" + prox + "' nao pode vir logo apos abrir parenteses '('.");
                }
                // nao pode ter '*' logo apos abrir parenteses, tipo '(*a)'
                if(prox == '*') 
                {
                    throw new IllegalArgumentException("o fechamento de kleene '*' nao pode vir logo apos abrir parenteses '('.");
                }
            }

            // caso 4: letras e numeros seguidos podem concatenar sem ponto, tipo 'aa' ou 'a1'
            // nas juncoes com parenteses ou depois de '*', a concatenacao continua explicita
            if(prox != '\0') 
            {
                boolean atualPodeTerminarTermo = isOperando(atual) || atual == '*' || atual == ')';
                boolean proxPodeComecarTermo = isOperando(prox) || prox == '(';

                boolean operandosSeguidos = isOperando(atual) && isOperando(prox);

                if(atualPodeTerminarTermo && proxPodeComecarTermo && !operandosSeguidos)
                {
                    throw new IllegalArgumentException(
                        "use o ponto '.' para concatenar com grupos ou apos '*'. falta '.' entre '" + atual + "' e '" + prox + "'.");
                }
            }
        }
    }
}
