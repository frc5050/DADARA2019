package frc.utils;

public class PidfConstants {
    /**
     * The proportional gain. Typically this is multiplied by the error term and added to the output.
     */
    public double p;

    /**
     * The integral gain. Typically multiplied by the integrated error (the area under the curve on the error vs time
     * graph, or the integral of e(t)dt throughout the run).
     */
    public double i;

    /**
     * The derivative gain. Typically multiplied by the change in error per time interval, i.e. the slope of the line
     * between the last two points on an error vs time graph.
     */
    public double d;

    /**
     * The feedforward term. Added independently to the output of a loop.
     */
    public double f;

    /**
     * The range the absolute value of the error must be within for the integral constant to take effect.
     */
    public double iZone;

    public PidfConstants() {
        this(0);
    }

    public PidfConstants(double p) {
        this(p, 0, 0);
    }

    public PidfConstants(double p, double i, double d) {
        this(p, i, d, 0);
    }

    public PidfConstants(double p, double i, double d, double f) {
        this(p, i, d, f, 0);
    }

    /**
     * Constructor.
     *
     * @param p     the proportional coefficient, see {@link PidfConstants#p} for additional information.
     * @param i     the integral coefficient, see {@link PidfConstants#i} for additional information.
     * @param d     the derivative coefficient, see {@link PidfConstants#d} for additional information.
     * @param f     the feedforward term, see {@link PidfConstants#f} for additional information.
     * @param iZone the integral accumulator zone, see {@link PidfConstants#iZone} for additional information.
     */
    public PidfConstants(double p, double i, double d, double f, double iZone) {
        this.p = p;
        this.i = i;
        this.d = d;
        this.f = f;
        this.iZone = iZone;
    }
}
