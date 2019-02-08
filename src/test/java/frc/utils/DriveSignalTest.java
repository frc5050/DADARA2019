package frc.utils;

import org.junit.Test;

import static frc.utils.Constants.EPSILON_SMALL_DOUBLE;
import static org.junit.Assert.*;

public class DriveSignalTest {
    private static final DriveSignal NEUTRAL = DriveSignal.NEUTRAL;
    private static final DriveSignal BRAKE = DriveSignal.BRAKE;
    private static final DriveSignal ONE_HUNDRED_PERCENT_FORWARD_BRAKE = new DriveSignal(1.0, 1.0, true);
    private static final DriveSignal ONE_HUNDRED_PERCENT_REVERSE_BRAKE = new DriveSignal(-1.0, -1.0, true);
    private static final DriveSignal ONE_HUNDRED_PERCENT_FORWARD_NO_BRAKE = new DriveSignal(1.0, 1.0, false);
    private static final DriveSignal ONE_HUNDRED_PERCENT_REVERSE_NO_BRAKE = new DriveSignal(-1.0, -1.0, false);
    private static final DriveSignal QUARTER_POWER_LEFT_TURN_BRAKE = new DriveSignal(-0.25, 0.25, true);
    private static final DriveSignal QUARTER_POWER_RIGHT_TURN_BRAKE = new DriveSignal(0.25, -0.25, true);
    private static final DriveSignal QUARTER_POWER_LEFT_TURN_NO_BRAKE = new DriveSignal(-0.25, 0.25, false);
    private static final DriveSignal QUARTER_POWER_RIGHT_TURN_NO_BRAKE = new DriveSignal(0.25, -0.25, false);

    @Test
    public void getLeftOutput() {
        assertEquals(0.0, NEUTRAL.getLeftOutput(), EPSILON_SMALL_DOUBLE);
        assertEquals(0.0, BRAKE.getLeftOutput(), EPSILON_SMALL_DOUBLE);
        assertEquals(1.0, ONE_HUNDRED_PERCENT_FORWARD_BRAKE.getLeftOutput(), EPSILON_SMALL_DOUBLE);
        assertEquals(-1.0, ONE_HUNDRED_PERCENT_REVERSE_BRAKE.getLeftOutput(), EPSILON_SMALL_DOUBLE);
        assertEquals(1.0, ONE_HUNDRED_PERCENT_FORWARD_NO_BRAKE.getLeftOutput(), EPSILON_SMALL_DOUBLE);
        assertEquals(-1.0, ONE_HUNDRED_PERCENT_REVERSE_NO_BRAKE.getLeftOutput(), EPSILON_SMALL_DOUBLE);
        assertEquals(0.25, QUARTER_POWER_RIGHT_TURN_BRAKE.getLeftOutput(), EPSILON_SMALL_DOUBLE);
        assertEquals(-0.25, QUARTER_POWER_LEFT_TURN_BRAKE.getLeftOutput(), EPSILON_SMALL_DOUBLE);
        assertEquals(0.25, QUARTER_POWER_RIGHT_TURN_NO_BRAKE.getLeftOutput(), EPSILON_SMALL_DOUBLE);
        assertEquals(-0.25, QUARTER_POWER_LEFT_TURN_NO_BRAKE.getLeftOutput(), EPSILON_SMALL_DOUBLE);
    }

    @Test
    public void getRightOutput() {
        assertEquals(0.0, NEUTRAL.getRightOutput(), EPSILON_SMALL_DOUBLE);
        assertEquals(0.0, BRAKE.getRightOutput(), EPSILON_SMALL_DOUBLE);
        assertEquals(1.0, ONE_HUNDRED_PERCENT_FORWARD_BRAKE.getRightOutput(), EPSILON_SMALL_DOUBLE);
        assertEquals(-1.0, ONE_HUNDRED_PERCENT_REVERSE_BRAKE.getRightOutput(), EPSILON_SMALL_DOUBLE);
        assertEquals(1.0, ONE_HUNDRED_PERCENT_FORWARD_NO_BRAKE.getRightOutput(), EPSILON_SMALL_DOUBLE);
        assertEquals(-1.0, ONE_HUNDRED_PERCENT_REVERSE_NO_BRAKE.getRightOutput(), EPSILON_SMALL_DOUBLE);
        assertEquals(-0.25, QUARTER_POWER_RIGHT_TURN_BRAKE.getRightOutput(), EPSILON_SMALL_DOUBLE);
        assertEquals(0.25, QUARTER_POWER_LEFT_TURN_BRAKE.getRightOutput(), EPSILON_SMALL_DOUBLE);
        assertEquals(-0.25, QUARTER_POWER_RIGHT_TURN_NO_BRAKE.getRightOutput(), EPSILON_SMALL_DOUBLE);
        assertEquals(0.25, QUARTER_POWER_LEFT_TURN_NO_BRAKE.getRightOutput(), EPSILON_SMALL_DOUBLE);
    }

    @Test
    public void getBrakeMode() {
        assertFalse(NEUTRAL.getBrakeMode());
        assertTrue(BRAKE.getBrakeMode());
        assertTrue(ONE_HUNDRED_PERCENT_FORWARD_BRAKE.getBrakeMode());
        assertTrue(ONE_HUNDRED_PERCENT_REVERSE_BRAKE.getBrakeMode());
        assertFalse(ONE_HUNDRED_PERCENT_FORWARD_NO_BRAKE.getBrakeMode());
        assertFalse(ONE_HUNDRED_PERCENT_REVERSE_NO_BRAKE.getBrakeMode());
        assertTrue(QUARTER_POWER_RIGHT_TURN_BRAKE.getBrakeMode());
        assertTrue(QUARTER_POWER_LEFT_TURN_BRAKE.getBrakeMode());
        assertFalse(QUARTER_POWER_RIGHT_TURN_NO_BRAKE.getBrakeMode());
        assertFalse(QUARTER_POWER_LEFT_TURN_NO_BRAKE.getBrakeMode());
    }
}
