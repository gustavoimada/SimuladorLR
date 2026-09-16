package br.com.simulador.at;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class TelaMenuInicial {
    private final BorderPane raiz = new BorderPane();

    public TelaMenuInicial(Runnable abrirAutomato, Runnable abrirER) {
        raiz.setTop(criarTopo());
        raiz.setCenter(criarCentro(abrirAutomato, abrirER));
        raiz.setStyle("-fx-background-color: #eef2f6; -fx-font-family: 'Segoe UI', Arial, sans-serif;");
    }

    public Parent getRaiz() {
        return raiz;
    }

    private HBox criarTopo() {
        Label titulo = new Label("Simulador de Linguagens Regulares");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: 700; -fx-text-fill: #172033;");

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);

        HBox topo = new HBox(12, titulo, espaco);
        topo.setAlignment(Pos.CENTER_LEFT);
        topo.setPadding(new Insets(16, 18, 12, 18));
        topo.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #d7dee8; -fx-border-width: 0 0 1 0;");
        return topo;
    }

    private VBox criarCentro(Runnable abrirAutomato, Runnable abrirER) {
        Label subtitulo = new Label("Escolha a representacao");
        subtitulo.setStyle("-fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #172033;");

        Button botaoAutomato = Estilo.botaoPrimario("Automatos Finitos");
        botaoAutomato.setPrefWidth(260);
        botaoAutomato.setOnAction(evento -> abrirAutomato.run());

        Button botaoER = Estilo.botaoSecundario("Expressoes Regulares");
        botaoER.setPrefWidth(260);
        botaoER.setOnAction(evento -> abrirER.run());

        Label detalhe = new Label("AFD/AFND por diagrama ou ER por expressao textual.");
        detalhe.setStyle("-fx-text-fill: #475569;");

        VBox centro = new VBox(14, subtitulo, botaoAutomato, botaoER, detalhe);
        centro.setAlignment(Pos.CENTER);
        centro.setPadding(new Insets(24));
        return centro;
    }
}
