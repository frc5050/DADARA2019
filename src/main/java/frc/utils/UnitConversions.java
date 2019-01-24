package frc.utils;

public class UnitConversions {
    private static final double TAU = 2 * Math.PI; // 2 pi
    private static final double RPM_TO_RAD_PER_SEC = TAU * secondsToMinutes(1);

    private UnitConversions() {

    }

    public static double inchesToMeters(double inches) {
        return inches * 0.0254;
    }

    public static double rpmToRadPerSec(double rpm) {
        // (rotations / minute) * (1 minute / 60 seconds) * (2 * pi / rotation) = (rotations / second)
        return rotationsToRadians(rpm) * secondsToMinutes(1.0);
    }

    public static double rotationsToRadians(double rotations) {
        return rotations * TAU;
    }

    public static double secondsToMinutes(double seconds) {
        return seconds / 60.0;
    }
}