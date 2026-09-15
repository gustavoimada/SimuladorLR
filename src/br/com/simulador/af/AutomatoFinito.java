package br.com.simulador.af;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AutomatoFinito {
    private final Map<String, Estado> estados = new LinkedHashMap<>();
    private final List<Transicao> transicoes = new ArrayList<>();

    public Estado adicionarEstado(String nome, double x, double y) {
        String nomeTratado = validarNomeEstado(nome);

        if(estados.containsKey(nomeTratado)) {
            throw new IllegalArgumentException("Ja existe um estado chamado " + nomeTratado + ".");
        }

        Estado estado = new Estado(nomeTratado, x, y);
        estados.put(nomeTratado, estado);

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

        if(!estado.getNome().equals(nomeTratado) && estados.containsKey(nomeTratado)) {
            throw new IllegalArgumentException("Ja existe um estado chamado " + nomeTratado + ".");
        }

        estados.remove(estado.getNome());
        estado.setNome(nomeTratado);
        estados.put(nomeTratado, estado);
    }

    public void removerEstado(Estado estado) {
        if(estado != null) {
            estados.remove(estado.getNome());
            transicoes.removeIf(transicao -> transicao.getOrigem() == estado || transicao.getDestino() == estado);

            if(estado.isInicial() && !estados.isEmpty()) {
                estados.values().iterator().next().setInicial(true);
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

        for(Estado atual : estados.values()) {
            atual.setInicial(false);
        }

        estado.setInicial(true);
    }

    public Estado getEstadoInicial() {
        Estado inicial = null;

        for(Estado estado : estados.values()) {
            if(estado.isInicial()) {
                inicial = estado;
            }
        }

        return inicial;
    }

    public List<Estado> getEstados() {
        return Collections.unmodifiableList(new ArrayList<>(estados.values()));
    }

    public List<Transicao> getTransicoes() {
        return Collections.unmodifiableList(transicoes);
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

    public Set<String> getAlfabeto() {
        Set<String> alfabeto = new LinkedHashSet<>();

        for(Transicao transicao : transicoes) {
            for(String simbolo : transicao.getSimbolos()) {
                if(!simbolo.equals(Transicao.EPSILON)) {
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

        Map<Estado, Set<String>> usadosPorEstado = new LinkedHashMap<>();

        for(Transicao transicao : transicoes) {
            if(transicao.possuiEpsilon()) {
                deterministico = false;
            }

            Set<String> usados = usadosPorEstado.computeIfAbsent(transicao.getOrigem(), chave -> new LinkedHashSet<>());
            for(String simbolo : transicao.getSimbolos()) {
                if(!simbolo.equals(Transicao.EPSILON) && usados.contains(simbolo)) {
                    deterministico = false;
                }
                usados.add(simbolo);
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
}
