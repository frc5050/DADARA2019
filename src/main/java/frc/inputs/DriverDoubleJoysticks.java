package frc.inputs;

import edu.wpi.first.wpilibj.Joystick;
import frc.utils.DriveHelper;
import frc.utils.DriveSignal;

import static frc.utils.Constants.DRIVER_JOYSTICK_LEFT_PORT;
import static frc.utils.Constants.DRIVER_JOYSTICK_RIGHT_PORT;

public class DriverDoubleJoysticks implements DriverHid {
    private static DriverDoubleJoysticks instance;

    private final Joystick joystickLeft;
    private final Joystick joystickRight;

    private DriverDoubleJoysticks() {
        joystickLeft = new Joystick(DRIVER_JOYSTICK_LEFT_PORT);
        joystickRight = new Joystick(DRIVER_JOYSTICK_RIGHT_PORT);
    }

    public static DriverDoubleJoysticks getInstance() {
        if (instance == null) {
            instance = new DriverDoubleJoysticks();
        }
        return instance;
    }

    @Override
    public void update() {

    }

    @Override
    public void disabled() {

    }

    @Override
    public void disabledPeriodic() {

    }

    @Override
    public DriveSignal getDriveSignal() {
        return DriveHelper.tankToDriveSignal(-joystickLeft.getRawAxis(1), -joystickRight.getRawAxis(1));
    }

    @Override
    public boolean liftAllJacks() {
        return false;
    }

    @Override
    public boolean retractAllJacks() {
        return false;
    }

    @Override
    public boolean initializeHabClimbing() {
        return false;
    }

    @Override
    public boolean manualJackWheelOverride() {
        return false;
    }

    @Override
    public boolean zeroJacks() {
        return false;
    }

    @Override
    public DriveSignal runJackWheels() {
        return null;
    }

    @Override
    public boolean cargoOuttakeRight() {
        return false;
    }

    @Override
    public boolean cargoOuttakeLeft() {
        return false;
    }

    @Override
    public boolean cargoIntakeRight() {
        return false;
    }

    @Override
    public boolean cargoIntakeLeft() {
        return false;
    }
}
