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


    // front 6, 4 extend/retract
    // left  7, 8 extend/retract
    // right 9 10 extend/retract
    // some random axis wheels

    @Override
    public boolean extendFrontJack() {
        return driverJoystick.getRawButton(6);
    }

    @Override
    public boolean retractFrontJack() {
        return driverJoystick.getRawButton(4);
    }

    @Override
    public boolean extendLeftJack() {
        return driverJoystick.getRawButton(7);
    }

    @Override
    public boolean retractLeftJack() {
        return driverJoystick.getRawButton(8);
    }

    @Override
    public boolean extendRightJack() {
        return driverJoystick.getRawButton(9);
    }

    @Override
    public boolean retractRightJack() {
        return driverJoystick.getRawButton(9);
    }

    @Override
    public DriveSignal runWheels() {
        double speed = driverJoystick.getRawAxis(3);
        return new DriveSignal(speed, speed);
    }
}
