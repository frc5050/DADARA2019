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

    /**
     * Returns a static instance of the {@link DriverJoystick} class. If none has been created yet, the instance
     * is created. This enables multiple any other classes to use this class without having to pass an instance or take
     * the risk of trying to instantiate multiple instances of this class, which would result in errors.
     *
     * @return a static instance of the {@link DriverJoystick} subsystem.
     */
    public static DriverJoystick getInstance() {
        if (instance == null) {
            instance = new DriverJoystick();
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

    DriveHelper driveHelperForCurvature = new DriveHelper();

    @Override
    public DriveSignal getDriveSignal() {
        // if less than 15 degrees left/right
        boolean quickTurn = Math.abs((driverJoystick.getDirectionDegrees() % 180.0) - 90) <= 15.0;
        return driveHelperForCurvature.curvatureDrive(-driverJoystick.getRawAxis(1), driverJoystick.getRawAxis(0), quickTurn);
//        return DriveHelper.arcadeToDriveSignal(-driverJoystick.getRawAxis(1), driverJoystick.getRawAxis(0));
    }

    @Override
    public boolean liftAllJacks() {
        return driverJoystick.getRawButton(1);
    }

    @Override
    public boolean retractAllJacks() {
        return driverJoystick.getRawButton(4);
    }

    @Override
    public boolean initializeHabClimbing() {
        return driverJoystick.getRawButton(8);
    }

    @Override
    public boolean manualJackWheelOverride() {
        return driverJoystick.getRawButton(9);
    }

    @Override
    public boolean zeroJacks() {
        return driverJoystick.getRawButton(10);
    }

    @Override
    public DriveSignal runJackWheels() {
        return DriveSignal.NEUTRAL;
//        double speed = driverJoystick.getRawAxis(3);
//        return new DriveSignal(speed, speed);
    }

    @Override
    public boolean cargoOuttakeRight() {
        return driverJoystick.getRawButton(6);
    }

    @Override
    public boolean cargoOuttakeLeft() {
        return driverJoystick.getRawButton(5);
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
