package frc.inputs;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import frc.utils.DriveHelper;
import frc.utils.DriveSignal;

import static frc.utils.Constants.DRIVER_GAMEPAD_PORT;

/**
 * Implements {@link DriverHid} with an Xbox style gamepad.
 */
public class DriverGamepad implements DriverHid {
    private static DriverGamepad instance;
    private XboxController gamepad;
    private LastDpadState lastDpadState = LastDpadState.NONE;

    /**
     * Constructor.
     */
    private DriverGamepad() {
        gamepad = new XboxController(DRIVER_GAMEPAD_PORT);
    }

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
    public boolean initializeHabClimbing() {
        return gamepad.getXButtonPressed();
    }

    @Override
    public boolean manualJackWheelOverride() {
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
