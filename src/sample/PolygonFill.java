package sample;

import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.stream.Collectors;

public class PolygonFill {
    private Collection<Point> vertices;
    private List<ActiveEdge> allEdges;
    private Set<ActiveEdge> globalEdges;
    private Color fillColor;

    public PolygonFill(Color color, List<Polygon> polygons) {
        this.fillColor = color;
        this.vertices = vertices;

        allEdges = new LinkedList<>();
        for (Polygon p : polygons) {
            LinkedList<Edge> edges = p.getEdges();
            for (Edge e : edges) {
                addEdge(e.getBegin(), e.getEnd());
            }
        }
        globalEdges = allEdges.stream().filter(activeEdge -> activeEdge.slope != 0).collect(Collectors.toCollection(TreeSet::new));
    }

    private static int round(double value) {
        return (int) (value + 0.5);
    }

    private void addEdge(Point begin, Point end) {
        double yMin;
        double yMax;
        double xVal;
        double dx = end.getX() - begin.getX();
        double dy = end.getY() - begin.getY();
        double slope = dx == 0 ? Double.POSITIVE_INFINITY : dy / dx;
        if (begin.getY() == end.getY()) {
            yMin = yMax = begin.getY();
            xVal = Math.min(begin.getX(), end.getX());
        } else if (begin.getY() < end.getY()) {
            yMin = begin.getY();
            yMax = end.getY();
            xVal = begin.getX();
        } else {
            yMin = end.getY();
            yMax = begin.getY();
            xVal = end.getX();
        }
        ActiveEdge result = new ActiveEdge(yMin, yMax, xVal, slope, false);
        allEdges.add(result);
    }

    public void render(GraphicsContext gc, double delay) {
        TreeSet<ActiveEdge> globalEdges = new TreeSet<>(this.globalEdges);
        TreeSet<ActiveEdge> activeEdges = new TreeSet<>();
        PixelWriter writer = gc.getPixelWriter();
        double scanLine = globalEdges.first().yMin;
        double yMax = globalEdges.last().yMax;
        if (delay == 0) {
            while (scanLine < yMax) {
                double finalScanLine = scanLine;
                activeEdges.removeIf(edge -> edge.yMax == finalScanLine);
                Iterator<ActiveEdge> iter = globalEdges.iterator();
                while (iter.hasNext()) {
                    ActiveEdge edge = iter.next();
                    if (edge.yMin == scanLine) {
                        iter.remove();
                        activeEdges.add(new ActiveEdge(edge.yMin, edge.yMax, edge.xVal, edge.slope, true));
                    }
                }
                double x = Double.POSITIVE_INFINITY;
                short parity = 0;
                for (ActiveEdge activeEdge : activeEdges) {
                    if (parity == 1) {
                        int y = round(scanLine);
                        if (x == activeEdge.xVal) {
                            writer.setColor(round(x), y, fillColor);
                        } else {
                            for (; x < activeEdge.xVal; x++) {
                                writer.setColor(round(x), y, fillColor);
                            }
                        }
                        parity = 0;
                    } else {
                        x = activeEdge.xVal;
                        parity = 1;
                    }
                }
                activeEdges = activeEdges
                        .stream()
                        .map(edge ->
                                edge.slope == 0 ?
                                        edge :
                                        new ActiveEdge(edge.yMin,
                                                edge.yMax,
                                                edge.xVal + edge.mInverse,
                                                edge.slope,
                                                true)).collect(Collectors.toCollection(TreeSet::new));
                scanLine++;
            }
        } else {
            Timeline timeline = new Timeline();
            final DoubleProperty scanline = new SimpleDoubleProperty(scanLine);
            final ObjectProperty<TreeSet<ActiveEdge>> activeedges = new SimpleObjectProperty<>(activeEdges);
            AnimationTimer timer = new AnimationTimer() {
                @Override
                public void handle(long l) {
                    double finalScanLine = scanline.getValue();
                    activeedges.getValue().removeIf(edge -> edge.yMax == finalScanLine);
                    Iterator<ActiveEdge> iter = globalEdges.iterator();
                    while (iter.hasNext()) {
                        ActiveEdge edge = iter.next();
                        if (edge.yMin == scanline.getValue()) {
                            iter.remove();
                            activeedges.getValue().add(new ActiveEdge(edge.yMin, edge.yMax, edge.xVal, edge.slope, true));
                        }
                    }
                    double x = Double.POSITIVE_INFINITY;
                    short parity = 0;
                    for (ActiveEdge activeEdge : activeedges.getValue()) {
                        if (parity == 1) {
                            int y = round(scanline.getValue());
                            if (x == activeEdge.xVal) {
                                writer.setColor(round(x), y, fillColor);
                            } else {
                                for (; x < activeEdge.xVal; x++) {
                                    writer.setColor(round(x), y, fillColor);
                                }
                            }
                            parity = 0;
                        } else {
                            x = activeEdge.xVal;
                            parity = 1;
                        }
                    }
                    activeedges.setValue(activeedges.getValue()
                            .stream()
                            .map(edge ->
                                    edge.slope == 0 ?
                                            edge :
                                            new ActiveEdge(edge.yMin,
                                                    edge.yMax,
                                                    edge.xVal + edge.mInverse,
                                                    edge.slope,
                                                    true)).collect(Collectors.toCollection(TreeSet::new)));
                    scanline.setValue(scanline.getValue() + 1);
                }
            };
            timeline.setCycleCount((int) (yMax - scanLine));
            timeline.play();

            timer.start();
        }
    }


}
