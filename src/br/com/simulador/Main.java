package br.com.simulador;

import br.com.simulador.ui.TelaAutomato;
import br.com.simulador.ui.TelaER;
import br.com.simulador.ui.TelaMenuInicial;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private Scene cena;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        cena = new Scene(new TelaMenuInicial(this::abrirAutomato, this::abrirER).getRaiz(), 1220, 760);
        stage.setTitle("Simulador de Linguagens Regulares - LFA");
        stage.setMinWidth(1080);
        stage.setMinHeight(690);
        stage.setScene(cena);
        stage.show();
    }

    private void abrirMenu() {
        cena.setRoot(new TelaMenuInicial(this::abrirAutomato, this::abrirER).getRaiz());
    }

    private void abrirAutomato() {
        TelaAutomato tela = new TelaAutomato(this::abrirMenu);
        cena.setRoot(tela.getRaiz());
        tela.inicializar();
    }

    private void abrirER() {
        TelaER tela = new TelaER(this::abrirMenu);
        cena.setRoot(tela.getRaiz());
    }
}
