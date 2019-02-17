package frc.subsystem;

import static frc.utils.Constants.BOTTOM_DIST_FROM_GROUND;
import static frc.utils.UnitConversions.inchesToMeters;

public class ElevatorPosition {
    public static final ElevatorPosition HATCH_LOW = new ElevatorPosition(BOTTOM_DIST_FROM_GROUND);
    public static final ElevatorPosition HATCH_MID = new ElevatorPosition(inchesToMeters(38));
    public static final ElevatorPosition HATCH_HIGH = new ElevatorPosition(inchesToMeters(64 + 1));
    public static final ElevatorPosition CARGO_LOW = new ElevatorPosition(inchesToMeters(24));
    public static final ElevatorPosition CARGO_MID = new ElevatorPosition(inchesToMeters(49.25));
    public static final ElevatorPosition CARGO_HIGH = new ElevatorPosition(inchesToMeters(77.5));

    private double height;

    public ElevatorPosition(final double height) {
        this.height = height;
    }

    public final double getHeight() {
        return height;
    }

    public void setHeight(final double height) {
        this.height = height;
    }
}