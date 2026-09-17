package br.com.simulador.at;

import br.com.simulador.gr.GramaticaRegular;
import br.com.simulador.gr.ProducaoRegular;
import br.com.simulador.gr.ResultadoGramatica;
import br.com.simulador.gr.SimuladorGramatica;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class TelaGramatica {
    private final Runnable aoVoltarMenu;
    private final BorderPane raiz = new BorderPane();
    private final TextArea areaGramatica = new TextArea();
    private final TextArea areaDetalhes = new TextArea();
    private final TextField campoEntradaUnica = new TextField();
    private final TextArea areaEntradasMultiplas = new TextArea();
    private final ListView<String> listaResultados = new ListView<>();
    private final Label labelStatus = new Label("Pronto");

    private SimuladorGramatica simulador;

    public TelaGramatica(Runnable aoVoltarMenu) {
        this.aoVoltarMenu = aoVoltarMenu;
        raiz.setTop(criarTopo());
        raiz.setCenter(criarConteudo());
        raiz.setBottom(criarRodape());
        raiz.setStyle("-fx-background-color: #eef2f6; -fx-font-family: 'Segoe UI', Arial, sans-serif;");
    }

    public Parent getRaiz() {
        return raiz;
    }

    private HBox criarTopo() {
        Label titulo = new Label("Gramaticas Regulares");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: 700; -fx-text-fill: #172033;");

        Button botaoMenu = Estilo.botaoSecundario("Menu");
        botaoMenu.setOnAction(evento -> aoVoltarMenu.run());

        Button botaoLimpar = Estilo.botaoSecundario("Limpar");
        botaoLimpar.setOnAction(evento -> limpar());

        Button botaoExemplo = Estilo.botaoSecundario("Exemplo");
        botaoExemplo.setOnAction(evento -> carregarExemplo());

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);

        HBox topo = new HBox(12, titulo, espaco, botaoMenu, botaoLimpar, botaoExemplo);
        topo.setAlignment(Pos.CENTER_LEFT);
        topo.setPadding(new Insets(16, 18, 12, 18));
        topo.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #d7dee8; -fx-border-width: 0 0 1 0;");
        return topo;
    }

    private HBox criarConteudo() {
        VBox painelEntrada = criarPainelEntrada();
        VBox painelResultado = criarPainelResultado();

        HBox conteudo = new HBox(14, painelEntrada, painelResultado);
        conteudo.setPadding(new Insets(14));
        HBox.setHgrow(painelEntrada, Priority.ALWAYS);
        HBox.setHgrow(painelResultado, Priority.ALWAYS);
        return conteudo;
    }

    private VBox criarPainelEntrada() {
        areaGramatica.setPromptText("S -> aA | b\nA -> aS | E");
        areaGramatica.setPrefRowCount(7);
        areaGramatica.setStyle("-fx-font-family: Consolas, 'Courier New', monospace;");

        Button botaoValidar = Estilo.botaoPrimario("Validar gramatica");
        botaoValidar.setMaxWidth(Double.MAX_VALUE);
        botaoValidar.setOnAction(evento -> executarComTratamento(this::validarGramatica));

        Label regras = new Label(
            "Informe uma producao por linha. Use letras maiusculas para nao terminais e alternativas no formato aB, a ou E. O primeiro nao terminal sera o inicial."
        );
        regras.setWrapText(true);
        regras.setStyle("-fx-text-fill: #475569;");

        campoEntradaUnica.setPromptText("palavra ou E");
        Button botaoReconhecer = Estilo.botaoPrimario("Reconhecer");
        botaoReconhecer.setOnAction(evento -> executarComTratamento(this::reconhecerEntradaUnica));
        HBox linhaUnica = new HBox(8, campoEntradaUnica, botaoReconhecer);
        HBox.setHgrow(campoEntradaUnica, Priority.ALWAYS);

        areaEntradasMultiplas.setPromptText("uma palavra por linha; use E para palavra vazia");
        areaEntradasMultiplas.setPrefRowCount(6);

        Button botaoMultiplas = Estilo.botaoPrimario("Reconhecer lista");
        botaoMultiplas.setMaxWidth(Double.MAX_VALUE);
        botaoMultiplas.setOnAction(evento -> executarComTratamento(this::reconhecerEntradasMultiplas));

        VBox painel = new VBox(10,
            Estilo.tituloSecao("Gramatica Regular"),
            Estilo.rotuloCampo("Producoes"),
            areaGramatica,
            botaoValidar,
            regras,
            new Separator(),
            Estilo.tituloSecao("Entrada unica"),
            linhaUnica,
            new Separator(),
            Estilo.tituloSecao("Multiplas entradas"),
            areaEntradasMultiplas,
            botaoMultiplas
        );
        painel.setPadding(new Insets(14));
        painel.setPrefWidth(470);
        painel.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #d7dee8; -fx-background-radius: 6; -fx-border-radius: 6;");
        return painel;
    }

    private VBox criarPainelResultado() {
        areaDetalhes.setEditable(false);
        areaDetalhes.setWrapText(true);
        areaDetalhes.setPrefHeight(300);
        areaDetalhes.setStyle(Estilo.campoTextoResultado());

        listaResultados.setPrefHeight(280);

        VBox painel = new VBox(12,
            Estilo.tituloSecao("Detalhes da gramatica"),
            areaDetalhes,
            new Separator(),
            Estilo.tituloSecao("Resultados"),
            listaResultados
        );
        painel.setPadding(new Insets(14));
        painel.setPrefWidth(520);
        painel.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #d7dee8; -fx-background-radius: 6; -fx-border-radius: 6;");
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

    private void validarGramatica() {
        simulador = new SimuladorGramatica(areaGramatica.getText());
        areaDetalhes.setText(formatarDetalhes());
        listaResultados.setItems(FXCollections.observableArrayList());
        labelStatus.setText("Gramatica regular valida.");
    }

    private void reconhecerEntradaUnica() {
        garantirSimulador();
        ResultadoGramatica resultado = simulador.reconhecer(campoEntradaUnica.getText());
        listaResultados.setItems(FXCollections.observableArrayList(resultado.toString()));
        labelStatus.setText(resultado.toString());
    }

    private void reconhecerEntradasMultiplas() {
        garantirSimulador();
        List<String> resultados = new ArrayList<>();
        String[] linhas = areaEntradasMultiplas.getText().split("\\R", -1);

        for(String linha : linhas) {
            String palavra = linha.trim();
            if(!palavra.isEmpty()) {
                resultados.add(simulador.reconhecer(palavra).toString());
            }
        }

        listaResultados.setItems(FXCollections.observableArrayList(resultados));
        labelStatus.setText(resultados.size() + " entrada(s) reconhecida(s).");
    }

    private void garantirSimulador() {
        simulador = new SimuladorGramatica(areaGramatica.getText());
        areaDetalhes.setText(formatarDetalhes());
    }

    private String formatarDetalhes() {
        GramaticaRegular gramatica = simulador.getGramatica();
        StringBuilder detalhes = new StringBuilder();
        detalhes.append("Simbolo inicial: ").append(gramatica.getSimboloInicial()).append('\n');
        detalhes.append("Nao terminais:  ").append(gramatica.getNaoTerminais()).append('\n');
        detalhes.append("Terminais:      ").append(gramatica.getTerminais()).append('\n');
        detalhes.append("Producoes:").append('\n');

        for(ProducaoRegular producao : gramatica.getProducoes()) {
            detalhes.append("  ").append(producao).append('\n');
        }

        return detalhes.toString();
    }

    private void limpar() {
        simulador = null;
        areaGramatica.clear();
        campoEntradaUnica.clear();
        areaEntradasMultiplas.clear();
        areaDetalhes.clear();
        listaResultados.setItems(FXCollections.observableArrayList());
        labelStatus.setText("Pronto");
    }

    private void carregarExemplo() {
        areaGramatica.setText("S -> aA | b\nA -> aS | E");
        campoEntradaUnica.setText("aa");
        areaEntradasMultiplas.setText("E\naa\naab\nab\nb");
        executarComTratamento(this::validarGramatica);
    }

    private void executarComTratamento(Runnable acao) {
        try {
            acao.run();
        } catch(IllegalArgumentException e) {
            simulador = null;
            labelStatus.setText(e.getMessage());
        } catch(Exception e) {
            simulador = null;
            labelStatus.setText("Erro: " + e.getMessage());
        }
    }
}
