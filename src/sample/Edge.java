package sample;

public class Edge {
    private Point begin;
    private Point end;

    public Edge(Point begin, Point end) {
        this.begin = begin;
        this.end = end;
        this.beginInit = false;
        this.endInit = false;
    }

    public Point getBegin() {
        return begin;
    }

    public void setBegin(Point begin) {
        this.begin = begin;
    }

    public Point getEnd() {
        return end;
    }

    public void setEnd(Point end) {
        this.end = end;
    }

    private boolean beginInit;
    private boolean endInit;

    public boolean isBeginInit() {
        return beginInit;
    }

    public void setBeginInit(boolean beginInit) {
        this.beginInit = beginInit;
    }

    public boolean isEndInit() {
        return endInit;
    }

    public void setEndInit(boolean endInit) {
        this.endInit = endInit;
    }

    public void clear() {
        setEndInit(false);
        setBeginInit(false);
    }
}
