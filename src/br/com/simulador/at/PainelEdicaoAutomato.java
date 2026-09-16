package br.com.simulador.at;

import br.com.simulador.af.Estado;
import br.com.simulador.af.Transicao;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

public class PainelEdicaoAutomato {
    private final TextField campoNomeEstado = new TextField();
    private final CheckBox checkInicial = new CheckBox("Inicial");
    private final CheckBox checkFinal = new CheckBox("Final");
    private final ListView<Estado> listaEstados = new ListView<>();

    private final ComboBox<Estado> comboOrigem = new ComboBox<>();
    private final ComboBox<Estado> comboDestino = new ComboBox<>();
    private final TextField campoRotuloTransicao = new TextField();
    private final ListView<Transicao> listaTransicoes = new ListView<>();
    private final ScrollPane root;

    private boolean atualizando;

    public PainelEdicaoAutomato(
        Consumer<Estado> aoSelecionarEstado,
        Consumer<Transicao> aoSelecionarTransicao,
        Runnable aoAdicionarEstado,
        Runnable aoSalvarEstado,
        Runnable aoRemoverSelecionado,
        Runnable aoAdicionarTransicao,
        Runnable aoSalvarTransicao
    ) {
        configurarSelecao(aoSelecionarEstado, aoSelecionarTransicao);
        root = criarRoot(aoAdicionarEstado, aoSalvarEstado, aoRemoverSelecionado, aoAdicionarTransicao, aoSalvarTransicao);
    }

    public ScrollPane getRoot() {
        return root;
    }

    public String getNomeEstado() {
        return campoNomeEstado.getText();
    }

    public boolean isEstadoInicialMarcado() {
        return checkInicial.isSelected();
    }

    public boolean isEstadoFinalMarcado() {
        return checkFinal.isSelected();
    }

    public Estado getOrigemSelecionada() {
        return comboOrigem.getValue();
    }

    public Estado getDestinoSelecionado() {
        return comboDestino.getValue();
    }

    public String getRotuloTransicao() {
        return campoRotuloTransicao.getText();
    }

    public void atualizar(List<Estado> estados, List<Transicao> transicoes, Estado estadoSelecionado, Transicao transicaoSelecionada) {
        atualizando = true;
        listaEstados.setItems(FXCollections.observableArrayList(estados));
        listaTransicoes.setItems(FXCollections.observableArrayList(transicoes));
        comboOrigem.setItems(FXCollections.observableArrayList(estados));
        comboDestino.setItems(FXCollections.observableArrayList(estados));

        if(estadoSelecionado != null && estados.contains(estadoSelecionado)) {
            listaEstados.getSelectionModel().select(estadoSelecionado);
            comboOrigem.getSelectionModel().select(estadoSelecionado);

            if(comboDestino.getValue() == null) {
                comboDestino.getSelectionModel().select(estadoSelecionado);
            }
        }

        if(transicaoSelecionada != null && transicoes.contains(transicaoSelecionada)) {
            listaTransicoes.getSelectionModel().select(transicaoSelecionada);
            comboOrigem.getSelectionModel().select(transicaoSelecionada.getOrigem());
            comboDestino.getSelectionModel().select(transicaoSelecionada.getDestino());
        }

        atualizando = false;
        preencherFormulario(estadoSelecionado, transicaoSelecionada, estados, transicoes);
    }

    private ScrollPane criarRoot(
        Runnable aoAdicionarEstado,
        Runnable aoSalvarEstado,
        Runnable aoRemoverSelecionado,
        Runnable aoAdicionarTransicao,
        Runnable aoSalvarTransicao
    ) {
        VBox conteudo = new VBox(12);
        conteudo.setPadding(new Insets(14));
        conteudo.setStyle("-fx-background-color: #f8fafc;");
        conteudo.setPrefWidth(270);

        campoNomeEstado.setPromptText("q0");
        campoRotuloTransicao.setPromptText("ex: a,b,ε");

        Button botaoAdicionarEstado = Estilo.botaoPrimario("Adicionar estado");
        botaoAdicionarEstado.setMaxWidth(Double.MAX_VALUE);
        botaoAdicionarEstado.setOnAction(evento -> aoAdicionarEstado.run());

        Button botaoSalvarEstado = Estilo.botaoSecundario("Salvar estado");
        botaoSalvarEstado.setMaxWidth(Double.MAX_VALUE);
        botaoSalvarEstado.setOnAction(evento -> aoSalvarEstado.run());

        Button botaoRemoverSelecionado = Estilo.botaoPerigo("Remover selecionado");
        botaoRemoverSelecionado.setMaxWidth(Double.MAX_VALUE);
        botaoRemoverSelecionado.setOnAction(evento -> aoRemoverSelecionado.run());

        HBox flagsEstado = new HBox(12, checkInicial, checkFinal);
        flagsEstado.setAlignment(Pos.CENTER_LEFT);

        Button botaoAdicionarTransicao = Estilo.botaoPrimario("Adicionar transicao");
        botaoAdicionarTransicao.setMaxWidth(Double.MAX_VALUE);
        botaoAdicionarTransicao.setOnAction(evento -> aoAdicionarTransicao.run());

        Button botaoSalvarTransicao = Estilo.botaoSecundario("Salvar transicao");
        botaoSalvarTransicao.setMaxWidth(Double.MAX_VALUE);
        botaoSalvarTransicao.setOnAction(evento -> aoSalvarTransicao.run());

        conteudo.getChildren().addAll(
            Estilo.tituloSecao("Estados"),
            Estilo.rotuloCampo("Nome"),
            campoNomeEstado,
            flagsEstado,
            botaoAdicionarEstado,
            botaoSalvarEstado,
            botaoRemoverSelecionado,
            listaEstados,
            new Separator(),
            Estilo.tituloSecao("Transicoes"),
            Estilo.rotuloCampo("Origem"),
            comboOrigem,
            Estilo.rotuloCampo("Destino"),
            comboDestino,
            Estilo.rotuloCampo("Simbolos"),
            campoRotuloTransicao,
            botaoAdicionarTransicao,
            botaoSalvarTransicao,
            listaTransicoes
        );

        listaEstados.setPrefHeight(130);
        listaTransicoes.setPrefHeight(170);
        comboOrigem.setMaxWidth(Double.MAX_VALUE);
        comboDestino.setMaxWidth(Double.MAX_VALUE);

        ScrollPane scroll = new ScrollPane(conteudo);
        scroll.setFitToWidth(true);
        scroll.setPrefWidth(292);
        scroll.setStyle("-fx-background: #f8fafc; -fx-background-color: #f8fafc; -fx-border-color: #d7dee8; -fx-border-width: 0 1 0 0;");
        return scroll;
    }

    private void configurarSelecao(Consumer<Estado> aoSelecionarEstado, Consumer<Transicao> aoSelecionarTransicao) {
        listaEstados.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> {
            if(!atualizando && novo != null) {
                aoSelecionarEstado.accept(novo);
            }
        });

        listaTransicoes.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> {
            if(!atualizando && novo != null) {
                aoSelecionarTransicao.accept(novo);
            }
        });
    }

    private void preencherFormulario(
        Estado estadoSelecionado,
        Transicao transicaoSelecionada,
        List<Estado> estados,
        List<Transicao> transicoes
    ) {
        if(estadoSelecionado != null && estados.contains(estadoSelecionado)) {
            campoNomeEstado.setText(estadoSelecionado.getNome());
            checkInicial.setSelected(estadoSelecionado.isInicial());
            checkFinal.setSelected(estadoSelecionado.isAceitacao());
        } else if(transicaoSelecionada == null) {
            campoNomeEstado.clear();
            checkInicial.setSelected(false);
            checkFinal.setSelected(false);
        }

        if(transicaoSelecionada != null && transicoes.contains(transicaoSelecionada)) {
            comboOrigem.getSelectionModel().select(transicaoSelecionada.getOrigem());
            comboDestino.getSelectionModel().select(transicaoSelecionada.getDestino());
            campoRotuloTransicao.setText(transicaoSelecionada.getRotulo());
        } else if(transicaoSelecionada == null && estadoSelecionado == null) {
            comboOrigem.getSelectionModel().clearSelection();
            comboDestino.getSelectionModel().clearSelection();
            campoRotuloTransicao.clear();
        }
    }
}
