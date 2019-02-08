package frc.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test structure for {@link DriveHelper}.
 */
public class DriveHelperTest {
    // Note that there is never a brake parameter given, this is just to make it easy to switch the default in
    // DriveSignal without having to change tests or use a constant for it.
    private static final DriveSignal ONE_HUNDRED_PERCENT_FORWARD = new DriveSignal(1.0, 1.0);
    private static final DriveSignal ONE_HUNDRED_PERCENT_REVERSE = new DriveSignal(-1.0, -1.0);
    private static final DriveSignal ONE_HUNDRED_PERCENT_LEFT_SPIN = new DriveSignal(-1.0, 1.0);
    private static final DriveSignal ONE_HUNDRED_PERCENT_RIGHT_SPIN = new DriveSignal(1.0, -1.0);
    private static final DriveSignal ZERO_POWER = new DriveSignal(0.0, 0.0);

    @Test
    public void tankToDriveSignal() {
        // TODO should probably test tankToDriveSignal(leftSpeed, rightSpeed, squareInputs)
        //  and                      tankToDriveSignal(leftSpeed, rightSpeed, squareInputs, deadband) as well
        assertEquals(ONE_HUNDRED_PERCENT_FORWARD, DriveHelper.tankToDriveSignal(1.0, 1.0));
        assertEquals(ONE_HUNDRED_PERCENT_REVERSE, DriveHelper.tankToDriveSignal(-1.0, -1.0));
        assertEquals(ONE_HUNDRED_PERCENT_LEFT_SPIN, DriveHelper.tankToDriveSignal(-1.0, 1.0));
        assertEquals(ONE_HUNDRED_PERCENT_RIGHT_SPIN, DriveHelper.tankToDriveSignal(1.0, -1.0));
        assertEquals(ZERO_POWER, DriveHelper.tankToDriveSignal(DriveHelper.TANK_DEFAULT_DEADBAND, DriveHelper.TANK_DEFAULT_DEADBAND));
        assertEquals(ZERO_POWER, DriveHelper.tankToDriveSignal(DriveHelper.TANK_DEFAULT_DEADBAND / 2.0, DriveHelper.TANK_DEFAULT_DEADBAND / 2.0));
        assertEquals(ZERO_POWER, DriveHelper.tankToDriveSignal(-DriveHelper.TANK_DEFAULT_DEADBAND, -DriveHelper.TANK_DEFAULT_DEADBAND));
        assertEquals(ZERO_POWER, DriveHelper.tankToDriveSignal(-DriveHelper.TANK_DEFAULT_DEADBAND / 2.0, -DriveHelper.TANK_DEFAULT_DEADBAND / 2.0));
        assertEquals(ZERO_POWER, DriveHelper.tankToDriveSignal(DriveHelper.TANK_DEFAULT_DEADBAND, -DriveHelper.TANK_DEFAULT_DEADBAND));
        assertEquals(ZERO_POWER, DriveHelper.tankToDriveSignal(-DriveHelper.TANK_DEFAULT_DEADBAND, -DriveHelper.TANK_DEFAULT_DEADBAND));
        for (double i = -2.0; i <= 2.0; i += 0.02) {
            final DriveSignal output = DriveHelper.tankToDriveSignal(i, i);
            assertTrue((output.getLeftOutput() <= 1.0) && (output.getLeftOutput() >= -1.0));
        }
    }
}
