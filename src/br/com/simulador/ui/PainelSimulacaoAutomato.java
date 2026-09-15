package br.com.simulador.ui;

import br.com.simulador.af.PassoAutomato;
import br.com.simulador.af.ResultadoAutomato;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class PainelSimulacaoAutomato {
    private final TextField campoEntradaUnica = new TextField();
    private final TextArea areaResultadoUnico = new TextArea();
    private final TextArea areaEntradasMultiplas = new TextArea();
    private final ListView<String> listaResultadosMultiplos = new ListView<>();

    private final TextField campoEntradaPasso = new TextField();
    private final Label labelPassoAtual = new Label();
    private final ListView<String> listaPassos = new ListView<>();
    private final ScrollPane root;

    public PainelSimulacaoAutomato(
        Runnable aoReconhecerEntradaUnica,
        Runnable aoReconhecerEntradasMultiplas,
        Runnable aoIniciarPasso,
        Runnable aoPassoAnterior,
        Runnable aoProximoPasso
    ) {
        root = criarRoot(aoReconhecerEntradaUnica, aoReconhecerEntradasMultiplas, aoIniciarPasso, aoPassoAnterior, aoProximoPasso);
    }

    public ScrollPane getRoot() {
        return root;
    }

    public String getEntradaUnica() {
        return campoEntradaUnica.getText();
    }

    public String getEntradasMultiplas() {
        return areaEntradasMultiplas.getText();
    }

    public String getEntradaPasso() {
        return campoEntradaPasso.getText();
    }

    public void mostrarResultadoUnico(String texto) {
        areaResultadoUnico.setText(texto);
    }

    public void mostrarResultadosMultiplos(List<String> resultados) {
        listaResultadosMultiplos.setItems(FXCollections.observableArrayList(resultados));
    }

    public void mostrarPassos(ResultadoAutomato resultado, int indicePasso) {
        if(resultado == null || resultado.getPassos().isEmpty()) {
            limparPasso();
            return;
        }

        PassoAutomato passo = resultado.getPassos().get(indicePasso);
        labelPassoAtual.setText("Passo " + passo.getIndice() + ": " + passo.getDescricao() + " | ativos " + passo.getEstadosFormatados());

        List<String> linhas = new ArrayList<>();
        for(int i = 0; i < resultado.getPassos().size(); i++) {
            String marcador = i == indicePasso ? "> " : "  ";
            linhas.add(marcador + resultado.getPassos().get(i).toString());
        }

        listaPassos.setItems(FXCollections.observableArrayList(linhas));
        listaPassos.getSelectionModel().select(indicePasso);
    }

    public void limparResultados() {
        areaResultadoUnico.clear();
        listaResultadosMultiplos.setItems(FXCollections.observableArrayList());
    }

    public void limparPasso() {
        labelPassoAtual.setText("");
        listaPassos.setItems(FXCollections.observableArrayList());
    }

    public void preencherEntradasMultiplas(String texto) {
        areaEntradasMultiplas.setText(texto);
    }

    private ScrollPane criarRoot(
        Runnable aoReconhecerEntradaUnica,
        Runnable aoReconhecerEntradasMultiplas,
        Runnable aoIniciarPasso,
        Runnable aoPassoAnterior,
        Runnable aoProximoPasso
    ) {
        VBox conteudo = new VBox(12);
        conteudo.setPadding(new Insets(14));
        conteudo.setPrefWidth(330);
        conteudo.setStyle("-fx-background-color: #f8fafc;");

        campoEntradaUnica.setPromptText("palavra ou ε");
        Button botaoReconhecer = Estilo.botaoPrimario("Reconhecer");
        botaoReconhecer.setOnAction(evento -> aoReconhecerEntradaUnica.run());
        HBox linhaUnica = new HBox(8, campoEntradaUnica, botaoReconhecer);
        HBox.setHgrow(campoEntradaUnica, Priority.ALWAYS);

        areaResultadoUnico.setEditable(false);
        areaResultadoUnico.setWrapText(true);
        areaResultadoUnico.setPrefHeight(125);
        areaResultadoUnico.setStyle(Estilo.campoTextoResultado());

        areaEntradasMultiplas.setPromptText("uma palavra por linha");
        areaEntradasMultiplas.setPrefRowCount(5);
        Button botaoReconhecerMultiplas = Estilo.botaoPrimario("Reconhecer lista");
        botaoReconhecerMultiplas.setMaxWidth(Double.MAX_VALUE);
        botaoReconhecerMultiplas.setOnAction(evento -> aoReconhecerEntradasMultiplas.run());
        listaResultadosMultiplos.setPrefHeight(130);

        campoEntradaPasso.setPromptText("palavra ou ε");
        Button botaoIniciarPasso = Estilo.botaoPrimario("Iniciar");
        botaoIniciarPasso.setOnAction(evento -> aoIniciarPasso.run());
        Button botaoAnterior = Estilo.botaoSecundario("Anterior");
        botaoAnterior.setOnAction(evento -> aoPassoAnterior.run());
        Button botaoProximo = Estilo.botaoSecundario("Proximo");
        botaoProximo.setOnAction(evento -> aoProximoPasso.run());

        HBox linhaPasso = new HBox(8, campoEntradaPasso, botaoIniciarPasso);
        HBox.setHgrow(campoEntradaPasso, Priority.ALWAYS);
        HBox botoesPasso = new HBox(8, botaoAnterior, botaoProximo);
        botaoAnterior.setMaxWidth(Double.MAX_VALUE);
        botaoProximo.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(botaoAnterior, Priority.ALWAYS);
        HBox.setHgrow(botaoProximo, Priority.ALWAYS);

        labelPassoAtual.setStyle("-fx-font-weight: 700; -fx-text-fill: #172033;");
        listaPassos.setPrefHeight(145);

        conteudo.getChildren().addAll(
            Estilo.tituloSecao("Entrada unica"),
            linhaUnica,
            areaResultadoUnico,
            new Separator(),
            Estilo.tituloSecao("Multiplas entradas"),
            areaEntradasMultiplas,
            botaoReconhecerMultiplas,
            listaResultadosMultiplos,
            new Separator(),
            Estilo.tituloSecao("Passo a passo"),
            linhaPasso,
            botoesPasso,
            labelPassoAtual,
            listaPassos
        );

        ScrollPane scroll = new ScrollPane(conteudo);
        scroll.setFitToWidth(true);
        scroll.setPrefWidth(352);
        scroll.setStyle("-fx-background: #f8fafc; -fx-background-color: #f8fafc; -fx-border-color: #d7dee8; -fx-border-width: 0 0 0 1;");
        return scroll;
    }
}
