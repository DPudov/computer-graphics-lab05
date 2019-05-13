package sample;

import java.util.LinkedList;

public class Polygon {
    private LinkedList<Edge> edges;

    public Polygon() {
        this.edges = new LinkedList<>();
    }

    public Polygon(LinkedList<Edge> edges) {
        this.edges = edges;
    }

    public LinkedList<Edge> getEdges() {
        return edges;
    }

    public void setEdges(LinkedList<Edge> edges) {
        this.edges = edges;
    }
}
