package sample;

// Алгоритм с упорядоченным списком рёбер

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.PixelWriter;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.LinkedList;

public class Controller {
    @FXML
    Button clearAllButton;
    @FXML
    TextField inputXField;
    @FXML
    TextField inputYField;
    @FXML
    Button addPointButton;

    @FXML
    Canvas canvas;
    @FXML
    Label cursorLabel;

    @FXML
    ColorPicker fillPicker;
    @FXML
    ColorPicker backgroundPicker;
    @FXML
    ColorPicker boundPicker;

    @FXML
    Button fillPolygonButton;
    @FXML
    Button clojureButton;


    private final LinkedList<Edge> allEdges = new LinkedList<>();
    private final LinkedList<Edge> currentPolygon = new LinkedList<>();
    private final ArrayList<Polygon> polygons = new ArrayList<>();
    private Edge currentEdge = new Edge(new Point(0, 0), new Point(0, 0));

    @FXML
    public void initialize() {
        setupColors();
        setupCanvasListeners();

        fillPolygonButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            fillPolygon();
        });

        clojureButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            closePolygon();
        });

        clearAllButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            clearCanvas();
        });
    }

    private void setupCanvasListeners() {
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            MouseButton b = e.getButton();
            boolean hasShift = e.isShiftDown();
            boolean hasControl = e.isControlDown();

            if (b == MouseButton.PRIMARY && hasShift && hasControl) {
                //Прямая
                addPoint((int) e.getX(), (int) e.getY());
            } else if (b == MouseButton.PRIMARY && hasShift) {
                // горизонтальная
                addPointHorizontal((int) e.getX(), (int) e.getY());
            } else if (b == MouseButton.PRIMARY && hasControl) {
                // вертикальная
                addPointVertical((int) e.getX(), (int) e.getY());
            } else if (b == MouseButton.PRIMARY) {
                addPoint((int) e.getX(), (int) e.getY());
                // Прямая
            } else if (b == MouseButton.SECONDARY) {
                // замкнуть многоугольник
                closePolygon();
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, mouseEvent -> {
            cursorLabel.setText("Координата курсора: " + mouseEvent.getX() + " ; " + mouseEvent.getY());
        });
    }

    private void setupColors() {
        boundPicker.setValue(Color.BLACK);
        fillPicker.setValue(Color.WHITE);
        backgroundPicker.setValue(Color.WHITE);
        clearCanvas();
    }

    private void clearCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(backgroundPicker.getValue());
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        currentEdge.clear();
        polygons.clear();
        currentPolygon.clear();
        allEdges.clear();
    }

    private void fillPolygon() {
// todo заливка
    }

    private void closePolygon() {
        if (currentPolygon.size() > 1) {
            addPoint(currentPolygon.get(0).getBegin().getX(), currentPolygon.get(0).getBegin().getY());
            polygons.add(new Polygon(currentPolygon));
            currentPolygon.clear();
            currentEdge.setBeginInit(false);
            currentEdge.setEndInit(false);
        }
    }

    private void addPointHorizontal(int x, int y) {
        if (currentEdge.isEndInit()) {
            addPoint(x, currentEdge.getEnd().getY());
        } else if (currentEdge.isBeginInit()) {
            addPoint(x, currentEdge.getBegin().getY());
        } else {
            addPoint(x, y);
        }
    }

    private void addPointVertical(int x, int y) {
        if (currentEdge.isEndInit()) {
            addPoint(currentEdge.getEnd().getX(), y);
        } else if (currentEdge.isBeginInit()) {
            addPoint(currentEdge.getBegin().getX(), y);
        } else {
            addPoint(x, y);
        }
    }

    private void addPoint(int x, int y) {
        if (!currentEdge.isBeginInit()) {
            currentEdge.setBegin(new Point(x, y));
            currentEdge.setBeginInit(true);
        } else if (!currentEdge.isEndInit()) {
            currentEdge.setEnd(new Point(x, y));
            currentEdge.setEndInit(true);
            doUpdate();
        } else {
            currentEdge.setBegin(currentEdge.getEnd());
            currentEdge.setEnd(new Point(x, y));
            doUpdate();
        }
    }

    private void doUpdate() {
        allEdges.add(currentEdge);
        currentPolygon.add(currentEdge);
        drawLine(currentEdge);
        currentEdge = new Edge(new Point(currentEdge.getBegin()), currentEdge.getEnd());
        currentEdge.setBeginInit(true);
        currentEdge.setEndInit(true);
    }

    private void drawLine(Edge edge) {
        Point beg = edge.getBegin();
        Point end = edge.getEnd();
        drawLine(beg.getX(), beg.getY(), end.getX(), end.getY());
    }

    private void drawLine(int xBegin, int yBegin, int xEnd, int yEnd) {
        DigitalDiffAnalyzeDraw(xBegin, yBegin, xEnd, yEnd, boundPicker.getValue());
    }

    private static boolean isPoint(int x0, int y0, int xe, int ye) {
        return x0 == xe && y0 == ye;
    }

    private static int round(float value) {
        return (int) (value + 0.5f);
    }

    private void DigitalDiffAnalyzeDraw(int x0, int y0, int xe, int ye, Color color) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        PixelWriter writer = gc.getPixelWriter();
        if (isPoint(x0, y0, xe, ye)) {
            writer.setColor(x0, y0, color);
            return;
        }

        int diffX = Math.abs(xe - x0);
        int diffY = Math.abs(ye - y0);
        int len = diffX > diffY ? diffX : diffY;

        float dX = ((float) (xe - x0)) / len;
        float dY = ((float) (ye - y0)) / len;
        float curX = x0;
        float curY = y0;
        for (int i = 1; i < len + 1; i++) {
            writer.setColor(round(curX), round(curY), color);
            curX += dX;
            curY += dY;
        }
    }
}
