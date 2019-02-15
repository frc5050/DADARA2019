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
        double y = -driverJoystick.getRawAxis(1);
        double x = driverJoystick.getRawAxis(0);
        // double degrees = Math.atan(y / x) * 180.0 / Math.PI;
        // boolean quickTurn = Math.abs((degrees % 180.0)) <= 15.0;
        return driveHelperForCurvature.curvatureDrive(y, x);
//        return DriveHelper.arcadeToDriveSignal(-driverJoystick.getRawAxis(1), driverJoystick.getRawAxis(0));
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
        return driverJoystick.getRawButton(7);
    }

    @Override
    public boolean manualJackWheelOverride() {
        return false;
    }

    @Override
    public boolean zeroJacks() {
        return driverJoystick.getRawButton(2);
    }

    @Override
    public DriveSignal runJackWheels() {
        return DriveSignal.NEUTRAL;
//        double speed = driverJoystick.getRawAxis(3);
//        return new DriveSignal(speed, speed);
    }

    @Override
    public boolean cargoOuttakeRight() {
        return driverJoystick.getPOV(0) == 90;
    }

    @Override
    public boolean cargoOuttakeLeft() {
        return driverJoystick.getPOV(0) == 270;
    }

    @Override
    public boolean cargoIntakeRight() {
        return driverJoystick.getRawButton(1) && driverJoystick.getPOV(0) == 90;
    }

    @Override
    public boolean cargoIntakeLeft() {
        return driverJoystick.getRawButton(1) && driverJoystick.getPOV(0) == 270;
    }
}














