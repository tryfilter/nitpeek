package nitpeek.core.internal;

/**
 * Note that the specific confidence levels are not guaranteed to be stable
 */
public enum Confidence {
    MIN(0.0),
    LOW(0.25),
    MEDIUM(0.5),
    HIGH(0.75),
    MAX(1.0);

    private final double confidenceLevel;

    Confidence(double confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    public double value() {
        return confidenceLevel;
    }
}
