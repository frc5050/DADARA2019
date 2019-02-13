package frc.utils;

import org.junit.Assert;
import org.junit.Test;

import static frc.utils.Constants.EPSILON_SMALL_DOUBLE;

public class UnitConversionsTest {
    private static final double EPSILON_BILLION = 1E-6;
    private static final double ONE_METER = 39.37007874015748031496062992126; // inches
    private static final double MINUS_ONE_METER = -ONE_METER;
    private static final double ONE_BILLION_METERS = 1E9 * ONE_METER;
    private static final double ONE_INCH = 0.0254; // meters
    private static final double MINUS_ONE_INCH = -ONE_INCH;
    private static final double ONE_BILLION_METERS_IN_INCHES = 1E9 * ONE_INCH;
    private static final double SIXTY_RPM = 60.0;
    private static final double TWO_PI = 2.0 * Math.PI;

    @Test
    public void inchesToMeters() {
        Assert.assertEquals(1.0, UnitConversions.inchesToMeters(ONE_METER), EPSILON_SMALL_DOUBLE);
        Assert.assertEquals(-1.0, UnitConversions.inchesToMeters(MINUS_ONE_METER), EPSILON_SMALL_DOUBLE);
        Assert.assertEquals(0, UnitConversions.inchesToMeters(0), EPSILON_SMALL_DOUBLE);
        Assert.assertEquals(1E9, UnitConversions.inchesToMeters(ONE_BILLION_METERS), EPSILON_BILLION);
        Assert.assertEquals(1.0, UnitConversions.inchesToMeters(UnitConversions.metersToInches(1.0)), EPSILON_SMALL_DOUBLE);
        Assert.assertEquals(-1.0, UnitConversions.inchesToMeters(UnitConversions.metersToInches(-1.0)), EPSILON_SMALL_DOUBLE);
        Assert.assertEquals(1E9, UnitConversions.inchesToMeters(UnitConversions.metersToInches(1E9)), EPSILON_BILLION);
    }

    @Test
    public void metersToInches() {
        Assert.assertEquals(1.0, UnitConversions.metersToInches(ONE_INCH), EPSILON_SMALL_DOUBLE);
        Assert.assertEquals(-1.0, UnitConversions.metersToInches(MINUS_ONE_INCH), EPSILON_SMALL_DOUBLE);
        Assert.assertEquals(0, UnitConversions.metersToInches(0), EPSILON_SMALL_DOUBLE);
        Assert.assertEquals(1E9, UnitConversions.metersToInches(ONE_BILLION_METERS_IN_INCHES), EPSILON_BILLION);
        Assert.assertEquals(1.0, UnitConversions.inchesToMeters(UnitConversions.metersToInches(1.0)), EPSILON_SMALL_DOUBLE);
        Assert.assertEquals(-1.0, UnitConversions.inchesToMeters(UnitConversions.metersToInches(-1.0)), EPSILON_SMALL_DOUBLE);
        Assert.assertEquals(1E9, UnitConversions.inchesToMeters(UnitConversions.metersToInches(1E9)), EPSILON_BILLION);
    }

    @Test
    public void rpmToRadPerSec() {
        Assert.assertEquals(0.0, UnitConversions.rpmToRadPerSec(0.0), EPSILON_SMALL_DOUBLE);
        Assert.assertEquals(TWO_PI, UnitConversions.rpmToRadPerSec(SIXTY_RPM), EPSILON_SMALL_DOUBLE);
        Assert.assertEquals(-TWO_PI, UnitConversions.rpmToRadPerSec(-SIXTY_RPM), EPSILON_SMALL_DOUBLE);
        for (double i = -10.0; i < 200.0; i += 0.1) {
            Assert.assertEquals(TWO_PI * i, UnitConversions.rpmToRadPerSec(SIXTY_RPM * i), EPSILON_SMALL_DOUBLE);
        }
    }

    @Test
    public void rotationsToRadians() {
        Assert.assertEquals(0.0, UnitConversions.rotationsToRadians(0.0), EPSILON_SMALL_DOUBLE);
        Assert.assertEquals(TWO_PI, UnitConversions.rotationsToRadians(1.0), EPSILON_SMALL_DOUBLE);
        Assert.assertEquals(-TWO_PI, UnitConversions.rotationsToRadians(-1.0), EPSILON_SMALL_DOUBLE);
        for (double i = -10.0; i < 200.0; i += 0.1) {
            Assert.assertEquals(i * 2 * Math.PI, UnitConversions.rotationsToRadians(i), EPSILON_SMALL_DOUBLE);
        }
    }

    @Test
    public void secondsToMinutes() {
        Assert.assertEquals(0.0, UnitConversions.secondsToMinutes(0.0), EPSILON_SMALL_DOUBLE);
        Assert.assertEquals(1.0, UnitConversions.secondsToMinutes(60.0), EPSILON_SMALL_DOUBLE);
        Assert.assertEquals(20.6, UnitConversions.secondsToMinutes(1236.0), EPSILON_SMALL_DOUBLE);
        Assert.assertEquals(-1.0, UnitConversions.secondsToMinutes(-60.0), EPSILON_SMALL_DOUBLE);
        Assert.assertEquals(-2.0, UnitConversions.secondsToMinutes(-120.0), EPSILON_SMALL_DOUBLE);
        Assert.assertEquals(-20.6, UnitConversions.secondsToMinutes(-1236.0), EPSILON_SMALL_DOUBLE);
    }

    @Test
    public void minutesToSeconds() {
        Assert.assertEquals(0.0, UnitConversions.minutesToSeconds(0.0), EPSILON_SMALL_DOUBLE);
        Assert.assertEquals(60.0, UnitConversions.minutesToSeconds(1.0), EPSILON_SMALL_DOUBLE);
        Assert.assertEquals(1236.0, UnitConversions.minutesToSeconds(20.6), EPSILON_SMALL_DOUBLE);
        Assert.assertEquals(-60.0, UnitConversions.minutesToSeconds(-1.0), EPSILON_SMALL_DOUBLE);
        Assert.assertEquals(-120.0, UnitConversions.minutesToSeconds(-2.0), EPSILON_SMALL_DOUBLE);
        Assert.assertEquals(-1236.0, UnitConversions.minutesToSeconds(-20.6), EPSILON_SMALL_DOUBLE);
    }

    @Test
    public void encoderUnitsPerMinuteToDistancePerSecond() {
        final double oneToOneDeltaEncoder = 100; // revolutions
        final double oneToOneDeltaDistance = 100; // meters
        Assert.assertEquals(1.0, UnitConversions.encoderUnitsPerMinuteToDistancePerSecond(oneToOneDeltaEncoder, oneToOneDeltaDistance, SIXTY_RPM), EPSILON_SMALL_DOUBLE);
        Assert.assertEquals(2.0, UnitConversions.encoderUnitsPerMinuteToDistancePerSecond(oneToOneDeltaEncoder, oneToOneDeltaDistance, SIXTY_RPM * 2.0), EPSILON_SMALL_DOUBLE);
        Assert.assertEquals(-1.0, UnitConversions.encoderUnitsPerMinuteToDistancePerSecond(oneToOneDeltaEncoder, oneToOneDeltaDistance, -SIXTY_RPM), EPSILON_SMALL_DOUBLE);
        final double deltaEncoder = 200; // revolutions
        final double deltaDistance = 2; // meters
        final double rpm = 5000; // revolutions / minute
        final double expected = 5000 * 0.01 / 60.0;
        Assert.assertEquals(expected, UnitConversions.encoderUnitsPerMinuteToDistancePerSecond(deltaEncoder, deltaDistance, rpm), EPSILON_SMALL_DOUBLE);
    }

    @Test
    public void distancePerSecondToEncoderUnitsPerMinute() {
        final double oneToOneDeltaEncoder = 100; // revolutions
        final double oneToOneDeltaDistance = 100; // meters
        Assert.assertEquals(SIXTY_RPM, UnitConversions.distancePerSecondToEncoderUnitsPerMinute(oneToOneDeltaEncoder, oneToOneDeltaDistance, 1.0), EPSILON_SMALL_DOUBLE);
        Assert.assertEquals(2 * SIXTY_RPM, UnitConversions.distancePerSecondToEncoderUnitsPerMinute(oneToOneDeltaEncoder, oneToOneDeltaDistance, 1.0 * 2.0), EPSILON_SMALL_DOUBLE);
        Assert.assertEquals(-SIXTY_RPM, UnitConversions.distancePerSecondToEncoderUnitsPerMinute(oneToOneDeltaEncoder, oneToOneDeltaDistance, -1.0), EPSILON_SMALL_DOUBLE);
        final double deltaEncoder = 200; // revolutions
        final double deltaDistance = 2; // meters
        final double encoderDistancePerSecond = 5000 * 0.01 / 60.0;
        final double expected = 5000; // revolutions / minute
        Assert.assertEquals(expected, UnitConversions.distancePerSecondToEncoderUnitsPerMinute(deltaEncoder, deltaDistance, encoderDistancePerSecond), EPSILON_SMALL_DOUBLE);
    }
}
