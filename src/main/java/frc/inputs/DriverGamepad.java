package frc.inputs;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import frc.utils.DpadHelper;
import frc.utils.DriveHelper;
import frc.utils.DriveSignal;

import static frc.utils.Constants.DRIVER_GAMEPAD_PORT;
import static frc.utils.DpadHelper.LastDpadState;

/**
 * Implements {@link DriverHid} with an Xbox style gamepad.
 */
public final class DriverGamepad implements DriverHid {
    private static DriverGamepad instance;
    private final XboxController gamepad;
    private LastDpadState lastDpadState = LastDpadState.NONE;

    /**
     * Constructor.
     */
    private DriverGamepad() {
        gamepad = new XboxController(DRIVER_GAMEPAD_PORT);
    }

    /**
     * Returns a static instance of the {@link DriverGamepad} class. If none has been created yet, the instance
     * is created. This enables multiple any other classes to use this class without having to pass an instance or take
     * the risk of trying to instantiate multiple instances of this class, which would result in errors.
     *
     * @return a static instance of the {@link DriverGamepad} subsystem.
     */
    public static DriverGamepad getInstance() {
        if (instance == null) {
            instance = new DriverGamepad();
        }
        return instance;
    }

    @Override
    public void update() {
        int pov = gamepad.getPOV();
        lastDpadState = DpadHelper.lastDpadUpdate(lastDpadState, pov);
    }

    @Override
    public void disabled() {

    }

    @Override
    public void disabledPeriodic() {
        gamepad.getXButtonPressed();
        gamepad.getYButtonPressed();
    }

    @Override
    public DriveSignal getDriveSignal() {
        return DriveHelper.tankToDriveSignal(-gamepad.getY(GenericHID.Hand.kLeft), -gamepad.getY(GenericHID.Hand.kRight), false);
    }

    @Override
    public boolean liftAllJacks() {
        return gamepad.getAButton();
    }

    @Override
    public boolean retractAllJacks() {
        return gamepad.getBButton();
    }

    @Override
    public boolean initializeHabClimbingLevel3() {
        return gamepad.getXButtonPressed();
    }

    @Override
    public boolean initializeHabClimbingLevel2() {
        return false;
    }

    @Override
    public boolean manualJackOverride() {
        return gamepad.getStartButton();
    }

    @Override
    public boolean zeroJacks() {
        return gamepad.getYButtonPressed();
    }

    @Override
    public DriveSignal runJackWheels() {
//        double power = gamepad.getBumper(GenericHID.Hand.kRight) ? 0.8 : (gamepad.getBumper(GenericHID.Hand.kLeft) ? -0.8 : 0.0);
//        return new DriveSignal(power, power, true);
        return DriveSignal.NEUTRAL;
    }

    @Override
    public boolean cargoOuttakeRight() {
        return gamepad.getBumper(GenericHID.Hand.kRight);
    }

    @Override
    public boolean cargoOuttakeLeft() {
        return gamepad.getBumper(GenericHID.Hand.kLeft);
    }

    @Override
    public boolean cargoIntakeRight() {
        return lastDpadState == LastDpadState.RIGHT;
    }

    @Override
    public boolean cargoIntakeLeft() {
        return lastDpadState == LastDpadState.LEFT;
    }
}
