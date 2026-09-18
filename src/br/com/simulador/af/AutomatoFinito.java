package br.com.simulador.af;

import java.util.ArrayList;
import java.util.List;

public class AutomatoFinito {
    private final List<Estado> estados = new ArrayList<>();
    private final List<Transicao> transicoes = new ArrayList<>();

    public Estado adicionarEstado(String nome, double x, double y) {
        String nomeTratado = validarNomeEstado(nome);

        if(buscarEstadoPorNome(nomeTratado) != null) {
            throw new IllegalArgumentException("Ja existe um estado chamado " + nomeTratado + ".");
        }

        Estado estado = new Estado(nomeTratado, x, y);
        estados.add(estado);

        if(estados.size() == 1) {
            estado.setInicial(true);
        }

        return estado;
    }

    public void renomearEstado(Estado estado, String novoNome) {
        if(estado == null) {
            throw new IllegalArgumentException("Selecione um estado para renomear.");
        }

        String nomeTratado = validarNomeEstado(novoNome);
        Estado estadoComMesmoNome = buscarEstadoPorNome(nomeTratado);

        if(estadoComMesmoNome != null && estadoComMesmoNome != estado) {
            throw new IllegalArgumentException("Ja existe um estado chamado " + nomeTratado + ".");
        }

        estado.setNome(nomeTratado);
    }

    public void removerEstado(Estado estado) {
        if(estado != null) {
            estados.remove(estado);

            for(int i = transicoes.size() - 1; i >= 0; i--) {
                Transicao transicao = transicoes.get(i);
                if(transicao.getOrigem() == estado || transicao.getDestino() == estado) {
                    transicoes.remove(i);
                }
            }

            if(estado.isInicial() && !estados.isEmpty()) {
                estados.get(0).setInicial(true);
            }
        }
    }

    public Transicao adicionarTransicao(Estado origem, Estado destino, String rotulo) {
        if(origem == null || destino == null) {
            throw new IllegalArgumentException("Escolha origem e destino da transicao.");
        }

        Transicao transicao = new Transicao(origem, destino, rotulo);
        transicoes.add(transicao);
        return transicao;
    }

    public void removerTransicao(Transicao transicao) {
        transicoes.remove(transicao);
    }

    public void definirInicial(Estado estado) {
        if(estado == null) {
            throw new IllegalArgumentException("Selecione um estado inicial.");
        }

        for(Estado atual : estados) {
            atual.setInicial(false);
        }

        estado.setInicial(true);
    }

    public Estado getEstadoInicial() {
        Estado inicial = null;

        for(Estado estado : estados) {
            if(inicial == null && estado.isInicial()) {
                inicial = estado;
            }
        }

        return inicial;
    }

    public List<Estado> getEstados() {
        return estados;
    }

    public List<Transicao> getTransicoes() {
        return transicoes;
    }

    public List<Transicao> getTransicoesSaindoDe(Estado estado) {
        List<Transicao> encontradas = new ArrayList<>();

        for(Transicao transicao : transicoes) {
            if(transicao.getOrigem() == estado) {
                encontradas.add(transicao);
            }
        }

        return encontradas;
    }

    public List<String> getAlfabeto() {
        List<String> alfabeto = new ArrayList<>();

        for(Transicao transicao : transicoes) {
            for(String simbolo : transicao.getSimbolos()) {
                if(!simbolo.equals(Transicao.EPSILON) && !alfabeto.contains(simbolo)) {
                    alfabeto.add(simbolo);
                }
            }
        }

        return alfabeto;
    }

    public boolean isDeterministico() {
        boolean deterministico = true;

        if(getEstadoInicial() == null) {
            deterministico = false;
        }

        for(Transicao transicao : transicoes) {
            if(transicao.possuiEpsilon()) {
                deterministico = false;
            }

            for(String simbolo : transicao.getSimbolos()) {
                if(!simbolo.equals(Transicao.EPSILON) && possuiOutraTransicaoComMesmoSimbolo(transicao, simbolo)) {
                    deterministico = false;
                }
            }
        }

        return deterministico;
    }

    public void limpar() {
        transicoes.clear();
        estados.clear();
    }

    private String validarNomeEstado(String nome) {
        String nomeTratado = nome == null ? "" : nome.trim();

        if(nomeTratado.isEmpty()) {
            throw new IllegalArgumentException("O nome do estado nao pode ficar vazio.");
        }

        return nomeTratado;
    }

    private Estado buscarEstadoPorNome(String nome) {
        Estado encontrado = null;

        for(Estado estado : estados) {
            if(estado.getNome().equals(nome)) {
                encontrado = estado;
            }
        }

        return encontrado;
    }

    private boolean possuiOutraTransicaoComMesmoSimbolo(Transicao referencia, String simbolo) {
        int quantidade = 0;

        for(Transicao transicao : transicoes) {
            if(transicao.getOrigem() == referencia.getOrigem() && transicao.aceitaSimbolo(simbolo)) {
                quantidade++;
            }
        }

        return quantidade > 1;
    }
}
