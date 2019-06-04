package sample;

public class ActiveEdge implements Comparable<ActiveEdge> {
    public double yMin;
    public double yMax;
    public double xVal;
    public double slope;
    public double mInverse;

    private boolean active;

    public ActiveEdge(double yMin, double yMax, double xVal, double slope, boolean active) {
        this.yMin = yMin;
        this.yMax = yMax;
        this.xVal = xVal;
        this.slope = slope;
        this.mInverse = slope == Double.POSITIVE_INFINITY ? 0 : 1 / slope;
        this.active = active;
    }

    @Override
    public int compareTo(ActiveEdge activeEdge) {
        if (this == activeEdge) {
            return 0;
        }
        if (active) {
            return xVal < activeEdge.xVal ? -1 : 1;
        } else if (yMin < activeEdge.yMin) {
            return -1;
        } else if (yMin == activeEdge.yMin) {
            if (xVal < activeEdge.xVal) {
                return -1;
            } else if (xVal == activeEdge.xVal) {
                if (yMax < activeEdge.yMax) {
                    return -1;
                }
            }
        }
        return 1;
    }

    @Override
    public String toString() {
        return "ActiveEdge{" +
                "yMin=" + yMin +
                ", yMax=" + yMax +
                ", xVal=" + xVal +
                ", slope=" + slope +
                ", mInverse=" + mInverse +
                ", active=" + active +
                '}';
    }
}
