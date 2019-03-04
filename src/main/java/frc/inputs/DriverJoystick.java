package frc.inputs;

import edu.wpi.first.wpilibj.Joystick;
import frc.utils.Constants;
import frc.utils.DriveHelper;
import frc.utils.DriveSignal;

/**
 * An implementation of the Driver's controls when the robot is being driven
 * with a joystick.
 */
public final class DriverJoystick implements DriverHid {

    private static DriverJoystick instance;
    private Joystick driverJoystick;

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

    private final DriveHelper driveHelperForCurvature = new DriveHelper();

    @Override
    public DriveSignal getDriveSignal() {
        return DriveHelper.arcadeToDriveSignal(-driverJoystick.getRawAxis(1), driverJoystick.getRawAxis(0));
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
        return driverJoystick.getRawButton(6);
    }

    @Override
    public boolean initializeHabClimbingLevel2() {
        return driverJoystick.getRawButton(4);
    }

    @Override
    public boolean manualJackOverride() {
        return driverJoystick.getRawButton(5);
    }

    @Override
    public boolean zeroJacks() {
        return driverJoystick.getRawButton(2);
    }

    @Override
    public DriveSignal runJackWheels() {
        return DriveSignal.NEUTRAL;
    }

    @Override
    public boolean cargoOuttakeRight() {
      int pov = driverJoystick.getPOV(0);
      return pov == 90 || pov == 45 || pov == 135;
    }

    @Override
    public boolean cargoOuttakeLeft() {
      int pov = driverJoystick.getPOV(0);
      return pov == 270 || pov == 315 || pov == 225;
    }

    @Override
    public boolean cargoIntakeRight() {
        int pov = driverJoystick.getPOV(0);
        return driverJoystick.getRawButton(1) && (pov == 90 || pov == 45 || pov == 135);
    }

    @Override
    public boolean cargoIntakeLeft() {
        int pov = driverJoystick.getPOV(0);
        return driverJoystick.getRawButton(1) && (pov == 270 || pov == 315 || pov == 225);
    }
}