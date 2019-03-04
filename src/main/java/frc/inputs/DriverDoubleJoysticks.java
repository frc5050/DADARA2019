package frc.inputs;

import edu.wpi.first.wpilibj.Joystick;
import frc.utils.DriveHelper;
import frc.utils.DriveSignal;

import static frc.utils.Constants.DRIVER_JOYSTICK_LEFT_PORT;
import static frc.utils.Constants.DRIVER_JOYSTICK_RIGHT_PORT;

/**
 * Implements {@link DriverHid} with two flight sticks.
 */
public final class DriverDoubleJoysticks implements DriverHid {
    private static DriverDoubleJoysticks instance;

    private final Joystick joystickLeft;
    private final Joystick joystickRight;

    /**
     * Constructor.
     */
    private DriverDoubleJoysticks() {
        joystickLeft = new Joystick(DRIVER_JOYSTICK_LEFT_PORT);
        joystickRight = new Joystick(DRIVER_JOYSTICK_RIGHT_PORT);
    }

    /**
     * Returns a static instance of the {@link DriverDoubleJoysticks} class. If none has been created yet, the instance
     * is created. This enables multiple any other classes to use this class without having to pass an instance or take
     * the risk of trying to instantiate multiple instances of this class, which would result in errors.
     *
     * @return a static instance of the {@link DriverDoubleJoysticks} subsystem.
     */
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
    public boolean initializeHabClimbingLevel3() {
        return false;
    }

    @Override
    public boolean initializeHabClimbingLevel2() {
        return false;
    }

    @Override
    public boolean manualJackOverride() {
        return false;
    }

    @Override
    public boolean zeroJacks() {
        return false;
    }

    @Override
    public DriveSignal runJackWheels() {
        return DriveSignal.BRAKE;
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
