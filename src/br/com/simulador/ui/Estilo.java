package br.com.simulador.ui;

import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

final class Estilo {
    private Estilo() {
    }

    static Label tituloSecao(String texto) {
        Label label = new Label(texto);
        label.setStyle("-fx-font-size: 13px; -fx-font-weight: 800; -fx-text-fill: #172033;");
        return label;
    }

    static Label rotuloCampo(String texto) {
        Label label = new Label(texto);
        label.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: #475569;");
        return label;
    }

    static Button botaoPrimario(String texto) {
        Button botao = new Button(texto);
        botao.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: 700; -fx-background-radius: 6; -fx-padding: 7 10;");
        botao.setCursor(Cursor.HAND);
        return botao;
    }

    static Button botaoSecundario(String texto) {
        Button botao = new Button(texto);
        botao.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #172033; -fx-font-weight: 700; -fx-background-radius: 6; -fx-padding: 7 10;");
        botao.setCursor(Cursor.HAND);
        return botao;
    }

    static Button botaoPerigo(String texto) {
        Button botao = new Button(texto);
        botao.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #991b1b; -fx-font-weight: 700; -fx-background-radius: 6; -fx-padding: 7 10;");
        botao.setCursor(Cursor.HAND);
        return botao;
    }

    static String campoTextoResultado() {
        return "-fx-control-inner-background: #ffffff; -fx-text-fill: #172033; -fx-font-family: Consolas, 'Courier New', monospace;";
    }
}
