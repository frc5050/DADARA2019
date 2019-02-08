package frc.utils;

/**
 * Helper functions for converting between units.
 */
public class UnitConversions {
    private static final double TAU = 2 * Math.PI; // 2 pi
    private static final double RPM_TO_RAD_PER_SEC = TAU * secondsToMinutes(1);

    /**
     * Constructor. Private to prevent instantiation.
     */
    private UnitConversions() {

    }

    /**
     * Converts from inches to meters.
     *
     * @param inches the value in inches to convert to meters.
     * @return the same distance, converted to meters, with the sign of the input preserved.
     */
    public static double inchesToMeters(double inches) {
        return inches * 0.0254;
    }

    /**
     * Converts from meters to inches.
     *
     * @param meters the value in meters to convert to inches.
     * @return the same distance, converted to meters, with the sign of the input preserved.
     */
    public static double metersToInches(double meters) {
        return meters / 0.0254;
    }

    /**
     * Converts from rotations per minute (RPM) to radians per second.
     *
     * @param rpm the value in rotations per minute to convert to meters.
     * @return the same number of rotations per second, converted to radians per second, with the sign of the input
     * preserved.
     */
    public static double rpmToRadPerSec(double rpm) {
        // Long form: (rotations / minute) * (1 minute / 60 seconds) * (2 * pi / rotation) = (rotations / second)
        return rotationsToRadians(rpm) * secondsToMinutes(1.0);
    }

    /**
     * Converts from rotations to radians.
     *
     * @param rotations the value in number of rotations to convert to radians.
     * @return the same number of rotations, converted to radians, with the sign of the input preserved.
     */
    public static double rotationsToRadians(double rotations) {
        return rotations * TAU;
    }

    /**
     * Converts from seconds to minutes.
     *
     * @param seconds the value in seconds to convert to minutes.
     * @return the same time interval, converted to minutes, with the sign of the input preserved.
     */
    public static double secondsToMinutes(double seconds) {
        return seconds / 60.0;
    }
}