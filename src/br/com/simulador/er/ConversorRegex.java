package br.com.simulador.er;

// essa classe faz a transformacao da er teorica pra regex do java
public class ConversorRegex 
{
    public static String converter(String er) 
    {
        String resultado = "";

        if(er != null) 
        {
            // primeiro substituimos '+' por '|'
            // pq na materia de lfa o '+' costuma ser usado como uniao (ex: a+b eh igual a|b)
            // no regex padrao do java, o '+' significa 'uma ou mais vezes', entao tem q trocar pra '|'
            String comUniaoPadrao = er.replace('+', '|');

            // agora tratamos o operador '.' da concatenacao
            // no regex java, o '.' eh caractere curinga (pega qualquer coisa!),
            // em lfa, '.' eh concatenacao explicita. no regex java, concatenacao eh so botar um do lado do outro (justaposicao)
            // entao a gente remove todos os pontos '.' da expressao
            String semPontos = comUniaoPadrao.replace(".", "");

            // agora checamos se tem algum '|' solto no nivel principal (fora de parenteses)
            // tipo: se o usuario digitou a+b, virou a|b. Se a gente so colocar ^a|b$, o regex vai entender (^a) | (b$)
            // que aceita muitas coisas a mais, entao basciamente a gnete coloca um parenteses em volta disso
            // entao pra nao dar esse bug classico, se tiver uniao no nivel zero a gente envolve em parenteses
            boolean precisaParentesesExtras = temUniaoNoNivelZero(semPontos);

            String corpoRegex = semPontos;
            if(precisaParentesesExtras)
                corpoRegex = "(" + semPontos + ")";

            // colocamos o ^ no inicio e o $ no final
            resultado = "^" + corpoRegex + "$";
        }

        return resultado;
    }

    // funcao auxiliar pra verificar se tem '|' fora de parenteses
    private static boolean temUniaoNoNivelZero(String s) 
    {
        int nivelParenteses = 0;
        boolean achou = false;

        for(int i = 0; i < s.length() && !achou; i++) 
        {
            char c = s.charAt(i);
            if(c == '(') 
            {
                nivelParenteses++;
            } 
            else 
                if(c == ')') 
                {
                    nivelParenteses--;
                } 
            else 
                if(c == '|' && nivelParenteses == 0) 
                {
                    // achou um '|' solto no nivel raiz
                    achou = true;
                }
        }

        return achou;
    }
}
