package br.com.simulador.at;

import br.com.simulador.af.AutomatoFinito;
import br.com.simulador.af.Estado;
import br.com.simulador.af.PassoAutomato;
import br.com.simulador.af.ResultadoAutomato;
import br.com.simulador.af.SimuladorAutomato;
import br.com.simulador.af.Transicao;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

public class TelaAutomato {
    private final AutomatoFinito automato = new AutomatoFinito();
    private final Label labelTipoAutomato = new Label();
    private final Label labelStatus = new Label("Pronto");
    private final List<String> estadosDestacados = new ArrayList<>();
    private final Runnable aoVoltarMenu;

    private final DiagramaAutomato diagrama;
    private final PainelEdicaoAutomato painelEdicao;
    private final PainelSimulacaoAutomato painelSimulacao;
    private final BorderPane raiz;

    private Estado estadoSelecionado;
    private Transicao transicaoSelecionada;
    private ResultadoAutomato resultadoPasso;
    private int indicePasso;
    private int contadorEstados;

    public TelaAutomato() {
        this(null);
    }

    public TelaAutomato(Runnable aoVoltarMenu) {
        this.aoVoltarMenu = aoVoltarMenu;

        diagrama = new DiagramaAutomato(
            automato,
            this::selecionarEstadoPeloDiagrama,
            this::selecionarTransicaoPeloDiagrama,
            this::adicionarEstadoNoPonto
        );

        painelEdicao = new PainelEdicaoAutomato(
            this::selecionarEstadoPeloPainel,
            this::selecionarTransicaoPeloPainel,
            () -> executarComTratamento(this::adicionarEstadoPeloFormulario),
            () -> executarComTratamento(this::salvarEstadoSelecionado),
            () -> executarComTratamento(this::removerSelecionado),
            () -> executarComTratamento(this::adicionarTransicaoPeloFormulario),
            () -> executarComTratamento(this::salvarTransicaoSelecionada)
        );

        painelSimulacao = new PainelSimulacaoAutomato(
            () -> executarComTratamento(this::reconhecerEntradaUnica),
            () -> executarComTratamento(this::reconhecerEntradasMultiplas),
            () -> executarComTratamento(this::iniciarPassoAPasso),
            () -> mostrarPasso(indicePasso - 1),
            () -> mostrarPasso(indicePasso + 1)
        );

        raiz = criarRaiz();
    }

    public Parent getRaiz() {
        return raiz;
    }

    public void inicializar() {
        atualizarInterface();
    }

    private BorderPane criarRaiz() {
        BorderPane layout = new BorderPane();
        layout.setTop(criarTopo());
        layout.setLeft(painelEdicao.getRoot());
        layout.setCenter(criarCentro());
        layout.setRight(painelSimulacao.getRoot());
        layout.setBottom(criarRodape());
        layout.setStyle("-fx-background-color: #eef2f6; -fx-font-family: 'Segoe UI', Arial, sans-serif;");
        return layout;
    }

    private HBox criarTopo() {
        Label titulo = new Label("Simulador de Automatos Finitos");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: 700; -fx-text-fill: #172033;");

        Button botaoLimpar = Estilo.botaoSecundario("Novo");
        botaoLimpar.setOnAction(evento -> limparAutomato());

        Button botaoMenu = Estilo.botaoSecundario("Menu");
        botaoMenu.setOnAction(evento -> aoVoltarMenu.run());
        botaoMenu.setVisible(aoVoltarMenu != null);
        botaoMenu.setManaged(aoVoltarMenu != null);

        Button botaoExemploAFD = Estilo.botaoSecundario("Exemplo AFD");
        botaoExemploAFD.setOnAction(evento -> carregarExemploAFD());

        Button botaoExemploAFND = Estilo.botaoSecundario("Exemplo AFND");
        botaoExemploAFND.setOnAction(evento -> carregarExemploAFND());

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);

        HBox topo = new HBox(12, titulo, espaco, botaoMenu, botaoLimpar, botaoExemploAFD, botaoExemploAFND);
        topo.setAlignment(Pos.CENTER_LEFT);
        topo.setPadding(new Insets(16, 18, 12, 18));
        topo.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #d7dee8; -fx-border-width: 0 0 1 0;");
        return topo;
    }

    private BorderPane criarCentro() {
        BorderPane painel = new BorderPane();
        painel.setPadding(new Insets(14));

        StackPane moldura = new StackPane(diagrama.getRoot());
        moldura.setAlignment(Pos.CENTER);
        moldura.setStyle("-fx-background-color: #e7edf5;");

        painel.setTop(labelTipoAutomato);
        painel.setCenter(moldura);
        BorderPane.setMargin(labelTipoAutomato, new Insets(0, 0, 10, 0));
        return painel;
    }

    private HBox criarRodape() {
        labelStatus.setStyle("-fx-text-fill: #334155;");

        HBox rodape = new HBox(labelStatus);
        rodape.setAlignment(Pos.CENTER_LEFT);
        rodape.setPadding(new Insets(8, 14, 8, 14));
        rodape.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #d7dee8; -fx-border-width: 1 0 0 0;");
        return rodape;
    }

    private void adicionarEstadoNoPonto(double x, double y) {
        executarComTratamento(() -> {
            Estado estado = automato.adicionarEstado(proximoNomeEstado(), diagrama.limitarX(x), diagrama.limitarY(y));
            estadoSelecionado = estado;
            transicaoSelecionada = null;
            limparResultadoPasso();
            atualizarInterface();
            labelStatus.setText("Estado " + estado.getNome() + " adicionado.");
        });
    }

    private void adicionarEstadoPeloFormulario() {
        String nome = painelEdicao.getNomeEstado().trim();
        if(nome.isEmpty()) {
            nome = proximoNomeEstado();
        }

        double x = 120 + ((contadorEstados * 86) % 390);
        double y = 120 + ((contadorEstados * 53) % 330);
        Estado estado = automato.adicionarEstado(nome, x, y);
        estado.setAceitacao(painelEdicao.isEstadoFinalMarcado());

        if(painelEdicao.isEstadoInicialMarcado()) {
            automato.definirInicial(estado);
        }

        estadoSelecionado = estado;
        transicaoSelecionada = null;
        limparResultadoPasso();
        atualizarInterface();
        labelStatus.setText("Estado " + estado.getNome() + " adicionado.");
    }

    private void salvarEstadoSelecionado() {
        if(estadoSelecionado == null) {
            throw new IllegalArgumentException("Selecione um estado.");
        }

        automato.renomearEstado(estadoSelecionado, painelEdicao.getNomeEstado());
        estadoSelecionado.setAceitacao(painelEdicao.isEstadoFinalMarcado());

        if(painelEdicao.isEstadoInicialMarcado()) {
            automato.definirInicial(estadoSelecionado);
        } else {
            estadoSelecionado.setInicial(false);
        }

        limparResultadoPasso();
        atualizarInterface();
        labelStatus.setText("Estado salvo.");
    }

    private void adicionarTransicaoPeloFormulario() {
        Transicao transicao = automato.adicionarTransicao(
            painelEdicao.getOrigemSelecionada(),
            painelEdicao.getDestinoSelecionado(),
            painelEdicao.getRotuloTransicao()
        );
        transicaoSelecionada = transicao;
        estadoSelecionado = null;
        limparResultadoPasso();
        atualizarInterface();
        labelStatus.setText("Transicao adicionada.");
    }

    private void salvarTransicaoSelecionada() {
        if(transicaoSelecionada == null) {
            throw new IllegalArgumentException("Selecione uma transicao.");
        }

        if(painelEdicao.getOrigemSelecionada() == null || painelEdicao.getDestinoSelecionado() == null) {
            throw new IllegalArgumentException("Escolha origem e destino da transicao.");
        }

        transicaoSelecionada.setOrigem(painelEdicao.getOrigemSelecionada());
        transicaoSelecionada.setDestino(painelEdicao.getDestinoSelecionado());
        transicaoSelecionada.setRotulo(painelEdicao.getRotuloTransicao());
        limparResultadoPasso();
        atualizarInterface();
        labelStatus.setText("Transicao salva.");
    }

    private void removerSelecionado() {
        if(transicaoSelecionada != null) {
            automato.removerTransicao(transicaoSelecionada);
            transicaoSelecionada = null;
        } else if(estadoSelecionado != null) {
            automato.removerEstado(estadoSelecionado);
            estadoSelecionado = null;
        } else {
            throw new IllegalArgumentException("Selecione um estado ou uma transicao.");
        }

        limparResultadoPasso();
        atualizarInterface();
        labelStatus.setText("Item removido.");
    }

    private void reconhecerEntradaUnica() {
        ResultadoAutomato resultado = new SimuladorAutomato(automato).reconhecer(painelSimulacao.getEntradaUnica());
        painelSimulacao.mostrarResultadoUnico(formatarResultadoDetalhado(resultado));
        destacarUltimoPasso(resultado);
        labelStatus.setText(resultado.toString());
    }

    private void reconhecerEntradasMultiplas() {
        List<ResultadoAutomato> resultados = new SimuladorAutomato(automato).reconhecerMultiplas(painelSimulacao.getEntradasMultiplas());
        List<String> linhas = new ArrayList<>();

        for(ResultadoAutomato resultado : resultados) {
            linhas.add(resultado.toString());
        }

        painelSimulacao.mostrarResultadosMultiplos(linhas);
        estadosDestacados.clear();
        atualizarInterface();
        labelStatus.setText(resultados.size() + " entrada(s) reconhecida(s).");
    }

    private void iniciarPassoAPasso() {
        resultadoPasso = new SimuladorAutomato(automato).reconhecer(painelSimulacao.getEntradaPasso());
        mostrarPasso(0);
        labelStatus.setText("Simulacao passo a passo iniciada.");
    }

    private void mostrarPasso(int novoIndice) {
        if(resultadoPasso == null || resultadoPasso.getPassos().isEmpty()) {
            return;
        }

        if(novoIndice < 0) {
            novoIndice = 0;
        }

        if(novoIndice >= resultadoPasso.getPassos().size()) {
            novoIndice = resultadoPasso.getPassos().size() - 1;
        }

        indicePasso = novoIndice;
        PassoAutomato passo = resultadoPasso.getPassos().get(indicePasso);
        estadosDestacados.clear();
        estadosDestacados.addAll(passo.getEstadosAtivos());
        diagrama.atualizar(estadoSelecionado, transicaoSelecionada, estadosDestacados);
        painelSimulacao.mostrarPassos(resultadoPasso, indicePasso);

        if(indicePasso == resultadoPasso.getPassos().size() - 1) {
            labelStatus.setText(resultadoPasso.toString());
        }
    }

    private void destacarUltimoPasso(ResultadoAutomato resultado) {
        estadosDestacados.clear();

        if(!resultado.getPassos().isEmpty()) {
            estadosDestacados.addAll(resultado.getPassos().get(resultado.getPassos().size() - 1).getEstadosAtivos());
        }

        diagrama.atualizar(estadoSelecionado, transicaoSelecionada, estadosDestacados);
    }

    private String formatarResultadoDetalhado(ResultadoAutomato resultado) {
        StringBuilder sb = new StringBuilder();
        sb.append(resultado.toString()).append('\n');
        sb.append("Palavra testada: ").append(resultado.getPalavraTestada().isEmpty() ? "ε" : resultado.getPalavraTestada()).append('\n');
        sb.append("Passos:\n");

        for(PassoAutomato passo : resultado.getPassos()) {
            sb.append(" - ").append(passo.toString()).append('\n');
        }

        return sb.toString();
    }

    private void limparResultadoPasso() {
        resultadoPasso = null;
        indicePasso = 0;
        estadosDestacados.clear();
        painelSimulacao.limparPasso();
    }

    private void atualizarInterface() {
        painelEdicao.atualizar(automato.getEstados(), automato.getTransicoes(), estadoSelecionado, transicaoSelecionada);
        diagrama.atualizar(estadoSelecionado, transicaoSelecionada, estadosDestacados);
        atualizarTipoAutomato();
    }

    private void selecionarEstadoPeloPainel(Estado estado) {
        estadoSelecionado = estado;
        transicaoSelecionada = null;
        atualizarInterface();
    }

    private void selecionarTransicaoPeloPainel(Transicao transicao) {
        transicaoSelecionada = transicao;
        estadoSelecionado = null;
        atualizarInterface();
    }

    private void selecionarEstadoPeloDiagrama(Estado estado) {
        estadoSelecionado = estado;
        transicaoSelecionada = null;
        painelEdicao.atualizar(automato.getEstados(), automato.getTransicoes(), estadoSelecionado, transicaoSelecionada);
    }

    private void selecionarTransicaoPeloDiagrama(Transicao transicao) {
        transicaoSelecionada = transicao;
        estadoSelecionado = null;
        painelEdicao.atualizar(automato.getEstados(), automato.getTransicoes(), estadoSelecionado, transicaoSelecionada);
    }

    private void atualizarTipoAutomato() {
        String tipo = automato.isDeterministico() ? "AFD" : "AFND";
        String alfabeto = automato.getAlfabeto().isEmpty() ? "{}" : automato.getAlfabeto().toString();
        labelTipoAutomato.setText("Tipo detectado: " + tipo + "   Estados: " + automato.getEstados().size() + "   Transicoes: " + automato.getTransicoes().size() + "   Alfabeto: " + alfabeto);
        labelTipoAutomato.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #172033; -fx-background-color: #f8fafc; -fx-background-radius: 6; -fx-border-color: #d7dee8; -fx-border-radius: 6; -fx-padding: 8 10;");
    }

    private void limparAutomato() {
        automato.limpar();
        contadorEstados = 0;
        estadoSelecionado = null;
        transicaoSelecionada = null;
        limparResultadoPasso();
        painelSimulacao.limparResultados();
        atualizarInterface();
        labelStatus.setText("Automato limpo.");
    }

    private void carregarExemploAFD() {
        automato.limpar();
        contadorEstados = 0;

        Estado q0 = automato.adicionarEstado("q0", 135, 305);
        Estado q1 = automato.adicionarEstado("q1", 330, 190);
        Estado q2 = automato.adicionarEstado("q2", 520, 305);
        q2.setAceitacao(true);
        automato.definirInicial(q0);

        automato.adicionarTransicao(q0, q1, "a");
        automato.adicionarTransicao(q0, q0, "b");
        automato.adicionarTransicao(q1, q1, "a");
        automato.adicionarTransicao(q1, q2, "b");
        automato.adicionarTransicao(q2, q1, "a");
        automato.adicionarTransicao(q2, q0, "b");

        contadorEstados = 3;
        estadoSelecionado = q0;
        transicaoSelecionada = null;
        limparResultadoPasso();
        painelSimulacao.limparResultados();
        painelSimulacao.preencherEntradasMultiplas("ab\nabab\naabb\nbaba");
        atualizarInterface();
        labelStatus.setText("Exemplo AFD carregado: palavras que terminam com ab.");
    }

    private void carregarExemploAFND() {
        automato.limpar();
        contadorEstados = 0;

        Estado p0 = automato.adicionarEstado("p0", 135, 305);
        Estado p1 = automato.adicionarEstado("p1", 330, 305);
        Estado p2 = automato.adicionarEstado("p2", 520, 305);
        p2.setAceitacao(true);
        automato.definirInicial(p0);

        automato.adicionarTransicao(p0, p1, "ε");
        automato.adicionarTransicao(p0, p0, "a");
        automato.adicionarTransicao(p1, p2, "b");
        automato.adicionarTransicao(p2, p2, "b");

        contadorEstados = 3;
        estadoSelecionado = p0;
        transicaoSelecionada = null;
        limparResultadoPasso();
        painelSimulacao.limparResultados();
        painelSimulacao.preencherEntradasMultiplas("b\nab\naab\naba");
        atualizarInterface();
        labelStatus.setText("Exemplo AFND carregado: usa transicao epsilon.");
    }

    private String proximoNomeEstado() {
        String nome;

        do {
            nome = "q" + contadorEstados;
            contadorEstados++;
        } while(existeEstadoComNome(nome));

        return nome;
    }

    private boolean existeEstadoComNome(String nome) {
        boolean existe = false;

        for(Estado estado : automato.getEstados()) {
            if(estado.getNome().equals(nome)) {
                existe = true;
            }
        }

        return existe;
    }

    private void executarComTratamento(Runnable acao) {
        try {
            acao.run();
        } catch(IllegalArgumentException e) {
            labelStatus.setText(e.getMessage());
        } catch(Exception e) {
            labelStatus.setText("Erro: " + e.getMessage());
        }
    }
}
