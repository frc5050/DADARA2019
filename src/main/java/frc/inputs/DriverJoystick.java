package frc.inputs;

import edu.wpi.first.wpilibj.Joystick;
import frc.utils.Constants;
import frc.utils.DriveHelper;
import frc.utils.DriveSignal;

/**
 * An implementation of the Driver's controls when the robot is being driven
 * with a joystick.
 */
public class DriverJoystick implements DriverHid {

    private static DriverJoystick instance;
    private final Joystick driverJoystick;

    private DriverJoystick() {
        driverJoystick = new Joystick(Constants.DRIVER_JOYSTICK_PORT);
    }

    public static DriverJoystick getInstance() {
        if (instance == null) {
            instance = new DriverJoystick();
        }
        return instance;
    }

    @Override
    public DriveSignal getDriveSignal() {
        return DriveHelper.arcadeToDriveSignal(-driverJoystick.getRawAxis(1), driverJoystick.getRawAxis(0));
    }

    @Override
    public boolean liftJack() {
        return driverJoystick.getRawButton(6);
    }
}
