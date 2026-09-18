package br.com.simulador.at;

import br.com.simulador.af.AutomatoFinito;
import br.com.simulador.af.Estado;
import br.com.simulador.af.Transicao;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.QuadCurve;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DiagramaAutomato {
    static final double RAIO_ESTADO = 28.0;
    static final double LARGURA = 650.0;
    static final double ALTURA = 610.0;

    private final AutomatoFinito automato;
    private final Pane area = new Pane();
    private final Pane camadaTransicoes = new Pane();
    private final Pane camadaEstados = new Pane();
    private final Consumer<Estado> aoSelecionarEstado;
    private final Consumer<Transicao> aoSelecionarTransicao;
    private final BiConsumer<Double, Double> aoCriarEstado;

    private Estado estadoSelecionado;
    private Transicao transicaoSelecionada;
    private final List<String> estadosDestacados = new ArrayList<>();

    public DiagramaAutomato(
        AutomatoFinito automato,
        Consumer<Estado> aoSelecionarEstado,
        Consumer<Transicao> aoSelecionarTransicao,
        BiConsumer<Double, Double> aoCriarEstado
    ) {
        this.automato = automato;
        this.aoSelecionarEstado = aoSelecionarEstado;
        this.aoSelecionarTransicao = aoSelecionarTransicao;
        this.aoCriarEstado = aoCriarEstado;
        configurarArea();
    }

    public Pane getRoot() {
        return area;
    }

    public double limitarX(double x) {
        return limitar(x, RAIO_ESTADO + 16, LARGURA - RAIO_ESTADO - 16);
    }

    public double limitarY(double y) {
        return limitar(y, RAIO_ESTADO + 16, ALTURA - RAIO_ESTADO - 16);
    }

    public void atualizar(Estado estadoSelecionado, Transicao transicaoSelecionada, List<String> estadosDestacados) {
        this.estadoSelecionado = estadoSelecionado;
        this.transicaoSelecionada = transicaoSelecionada;
        this.estadosDestacados.clear();
        this.estadosDestacados.addAll(estadosDestacados);
        redesenharTransicoes();
        redesenharEstados();
    }

    private void configurarArea() {
        area.setPrefSize(LARGURA, ALTURA);
        area.setMinSize(420, 360);
        area.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 6; -fx-border-color: #cbd5e1; -fx-border-radius: 6;");
        camadaTransicoes.setPickOnBounds(false);
        camadaEstados.setPickOnBounds(false);
        area.getChildren().addAll(camadaTransicoes, camadaEstados);

        area.addEventHandler(MouseEvent.MOUSE_CLICKED, evento -> {
            if(evento.getButton() == MouseButton.PRIMARY && evento.getClickCount() == 2 && evento.getTarget() == area) {
                aoCriarEstado.accept(evento.getX(), evento.getY());
                evento.consume();
            }
        });
    }

    private void selecionarEstadoLocal(Estado estado, boolean redesenhar) {
        estadoSelecionado = estado;
        transicaoSelecionada = null;
        aoSelecionarEstado.accept(estado);

        if(redesenhar) {
            redesenharEstados();
            redesenharTransicoes();
        }
    }

    private void selecionarTransicaoLocal(Transicao transicao) {
        estadoSelecionado = null;
        transicaoSelecionada = transicao;
        aoSelecionarTransicao.accept(transicao);
        redesenharEstados();
        redesenharTransicoes();
    }

    private void redesenharEstados() {
        camadaEstados.getChildren().clear();

        for(Estado estado : automato.getEstados()) {
            camadaEstados.getChildren().add(criarNoEstado(estado));
        }
    }

    private Group criarNoEstado(Estado estado) {
        javafx.scene.layout.StackPane corpo = new javafx.scene.layout.StackPane();
        corpo.setPrefSize(RAIO_ESTADO * 2, RAIO_ESTADO * 2);
        corpo.setMinSize(RAIO_ESTADO * 2, RAIO_ESTADO * 2);
        corpo.setMaxSize(RAIO_ESTADO * 2, RAIO_ESTADO * 2);

        boolean selecionado = estado == estadoSelecionado;
        boolean destacado = estadosDestacados.contains(estado.getNome());
        String preenchimento = destacado ? "#fff7ed" : "#f8fafc";
        String borda = destacado ? "#f59e0b" : selecionado ? "#2563eb" : "#475569";
        double larguraBorda = destacado || selecionado ? 3.0 : 2.0;

        Circle circulo = new Circle(RAIO_ESTADO);
        circulo.setFill(Color.web(preenchimento));
        circulo.setStroke(Color.web(borda));
        circulo.setStrokeWidth(larguraBorda);
        corpo.getChildren().add(circulo);

        if(estado.isAceitacao()) {
            Circle circuloFinal = new Circle(RAIO_ESTADO - 6);
            circuloFinal.setFill(Color.TRANSPARENT);
            circuloFinal.setStroke(Color.web(borda));
            circuloFinal.setStrokeWidth(2.0);
            corpo.getChildren().add(circuloFinal);
        }

        Text nome = new Text(estado.getNome());
        nome.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        nome.setFill(Color.web("#172033"));
        corpo.getChildren().add(nome);

        Group grupo = new Group(corpo);
        grupo.setLayoutX(estado.getX() - RAIO_ESTADO);
        grupo.setLayoutY(estado.getY() - RAIO_ESTADO);
        grupo.setCursor(Cursor.HAND);

        if(estado.isInicial()) {
            Line linha = new Line(-38, RAIO_ESTADO, -7, RAIO_ESTADO);
            linha.setStroke(Color.web("#0f766e"));
            linha.setStrokeWidth(2.2);
            Polygon ponta = new Polygon(-7, RAIO_ESTADO, -16, RAIO_ESTADO - 6, -16, RAIO_ESTADO + 6);
            ponta.setFill(Color.web("#0f766e"));
            grupo.getChildren().addAll(linha, ponta);
        }

        DragContext drag = new DragContext();
        grupo.setOnMousePressed(evento -> {
            selecionarEstadoLocal(estado, false);
            Point2D ponto = area.sceneToLocal(evento.getSceneX(), evento.getSceneY());
            drag.deltaX = ponto.getX() - estado.getX();
            drag.deltaY = ponto.getY() - estado.getY();
            evento.consume();
        });

        grupo.setOnMouseDragged(evento -> {
            Point2D ponto = area.sceneToLocal(evento.getSceneX(), evento.getSceneY());
            estado.setX(limitarX(ponto.getX() - drag.deltaX));
            estado.setY(limitarY(ponto.getY() - drag.deltaY));
            grupo.setLayoutX(estado.getX() - RAIO_ESTADO);
            grupo.setLayoutY(estado.getY() - RAIO_ESTADO);
            redesenharTransicoes();
            evento.consume();
        });

        grupo.setOnMouseClicked(evento -> {
            if(evento.getButton() == MouseButton.PRIMARY) {
                selecionarEstadoLocal(estado, true);
                evento.consume();
            }
        });

        return grupo;
    }

    private void redesenharTransicoes() {
        camadaTransicoes.getChildren().clear();
        List<Transicao> transicoes = automato.getTransicoes();

        for(int i = 0; i < transicoes.size(); i++) {
            Transicao transicao = transicoes.get(i);
            int indice = contarTransicoesAnteriores(transicoes, i, transicao.getOrigem(), transicao.getDestino());
            int total = contarTransicoesDoPar(transicoes, transicao.getOrigem(), transicao.getDestino());

            if(transicao.getOrigem() == transicao.getDestino()) {
                camadaTransicoes.getChildren().add(criarLaco(transicao, indice));
            } else {
                boolean possuiReversa = possuiTransicaoEntre(transicoes, transicao.getDestino(), transicao.getOrigem());
                camadaTransicoes.getChildren().add(criarAresta(transicao, indice, total, possuiReversa));
            }
        }
    }

    private Group criarAresta(Transicao transicao, int indice, int total, boolean possuiReversa) {
        Estado origem = transicao.getOrigem();
        Estado destino = transicao.getDestino();

        double dx = destino.getX() - origem.getX();
        double dy = destino.getY() - origem.getY();
        double distancia = Math.max(Math.hypot(dx, dy), 1.0);
        double ux = dx / distancia;
        double uy = dy / distancia;
        double px = -uy;
        double py = ux;

        double afastamentoParalelo = (indice - (total - 1) / 2.0) * 24.0;
        double offset = possuiReversa ? 36.0 + afastamentoParalelo : afastamentoParalelo;

        double inicioX = origem.getX() + ux * RAIO_ESTADO + px * offset;
        double inicioY = origem.getY() + uy * RAIO_ESTADO + py * offset;
        double fimX = destino.getX() - ux * RAIO_ESTADO + px * offset;
        double fimY = destino.getY() - uy * RAIO_ESTADO + py * offset;
        double controleX = (origem.getX() + destino.getX()) / 2.0 + px * offset * 1.8;
        double controleY = (origem.getY() + destino.getY()) / 2.0 + py * offset * 1.8;

        boolean selecionada = transicao == transicaoSelecionada;
        QuadCurve curva = new QuadCurve(inicioX, inicioY, controleX, controleY, fimX, fimY);
        estilizarCurva(curva, selecionada);
        Polygon seta = criarSeta(fimX, fimY, fimX - controleX, fimY - controleY, selecionada);
        Label rotulo = criarRotuloTransicao(transicao, controleX, controleY);

        Group grupo = new Group(curva, seta, rotulo);
        configurarSelecaoTransicao(grupo, transicao);
        return grupo;
    }

    private Group criarLaco(Transicao transicao, int indice) {
        Estado estado = transicao.getOrigem();
        double deslocamento = indice * 18.0;
        double x = estado.getX();
        double y = estado.getY();
        boolean selecionada = transicao == transicaoSelecionada;

        CubicCurve curva = new CubicCurve(
            x - 16, y - RAIO_ESTADO + 4,
            x - 72, y - 82 - deslocamento,
            x + 72, y - 82 - deslocamento,
            x + 16, y - RAIO_ESTADO + 4
        );
        estilizarCurva(curva, selecionada);

        Polygon seta = criarSeta(x + 16, y - RAIO_ESTADO + 4, 1, 1, selecionada);
        Label rotulo = criarRotuloTransicao(transicao, x + 34, y - 90 - deslocamento);

        Group grupo = new Group(curva, seta, rotulo);
        configurarSelecaoTransicao(grupo, transicao);
        return grupo;
    }

    private void estilizarCurva(javafx.scene.shape.Shape curva, boolean selecionada) {
        curva.setFill(Color.TRANSPARENT);
        curva.setStroke(Color.web(selecionada ? "#d97706" : "#334155"));
        curva.setStrokeWidth(selecionada ? 3.0 : 2.2);
    }

    private Polygon criarSeta(double x, double y, double dx, double dy, boolean selecionada) {
        double angulo = Math.atan2(dy, dx);
        double tamanho = 10.0;
        double abertura = Math.toRadians(24);

        double x1 = x - tamanho * Math.cos(angulo - abertura);
        double y1 = y - tamanho * Math.sin(angulo - abertura);
        double x2 = x - tamanho * Math.cos(angulo + abertura);
        double y2 = y - tamanho * Math.sin(angulo + abertura);

        Polygon seta = new Polygon(x, y, x1, y1, x2, y2);
        seta.setFill(Color.web(selecionada ? "#d97706" : "#334155"));
        return seta;
    }

    private Label criarRotuloTransicao(Transicao transicao, double x, double y) {
        Label rotulo = new Label(transicao.getRotulo());
        rotulo.setStyle("-fx-background-color: rgba(255,255,255,0.92); -fx-border-color: #cbd5e1; -fx-border-radius: 4; -fx-background-radius: 4; -fx-padding: 2 6; -fx-font-weight: 700; -fx-text-fill: #172033;");
        rotulo.setLayoutX(x - 14);
        rotulo.setLayoutY(y - 12);
        return rotulo;
    }

    private void configurarSelecaoTransicao(Group grupo, Transicao transicao) {
        grupo.setCursor(Cursor.HAND);
        grupo.setOnMouseClicked(evento -> {
            if(evento.getButton() == MouseButton.PRIMARY) {
                selecionarTransicaoLocal(transicao);
                evento.consume();
            }
        });
    }

    private int contarTransicoesDoPar(List<Transicao> transicoes, Estado origem, Estado destino) {
        int total = 0;

        for(Transicao transicao : transicoes) {
            if(transicao.getOrigem() == origem && transicao.getDestino() == destino) {
                total++;
            }
        }

        return total;
    }

    private int contarTransicoesAnteriores(List<Transicao> transicoes, int limite, Estado origem, Estado destino) {
        int total = 0;

        for(int i = 0; i < limite; i++) {
            Transicao transicao = transicoes.get(i);
            if(transicao.getOrigem() == origem && transicao.getDestino() == destino) {
                total++;
            }
        }

        return total;
    }

    private boolean possuiTransicaoEntre(List<Transicao> transicoes, Estado origem, Estado destino) {
        boolean encontrou = false;

        for(Transicao transicao : transicoes) {
            if(transicao.getOrigem() == origem && transicao.getDestino() == destino) {
                encontrou = true;
            }
        }

        return encontrou;
    }

    private double limitar(double valor, double minimo, double maximo) {
        return Math.max(minimo, Math.min(maximo, valor));
    }

    private static class DragContext {
        private double deltaX;
        private double deltaY;
    }
}
