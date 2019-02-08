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
    public DriveSignal getDriveSignal() {
        return DriveHelper.tankToDriveSignal(-gamepad.getY(GenericHID.Hand.kLeft), -gamepad.getY(GenericHID.Hand.kRight), false);
//        TODO remove once above is confirmed to work return new DriveSignal(-gamepad.getY(GenericHID.Hand.kLeft), -gamepad.getY(GenericHID.Hand.kRight));
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
        boolean xButtonPressed = gamepad.getXButtonPressed();
        if (xButtonPressed) {
            System.out.println("X button pressed");
        }
        return xButtonPressed;
    }

    @Override
    public boolean manualJackWheelOverride() {
        return gamepad.getStartButton();
    }

    @Override
    public boolean zeroJacks() {
        boolean yButtonPressed = gamepad.getYButtonPressed();
        if (yButtonPressed) {
            System.out.println("Y button pressed");
        }
        return yButtonPressed;
    }

    @Override
    public DriveSignal runJackWheels() {
        double power = gamepad.getBumper(GenericHID.Hand.kRight) ? 0.8 : (gamepad.getBumper(GenericHID.Hand.kLeft) ? -0.8 : 0.0);
        return new DriveSignal(power, power, true);
    }

    @Override
    public boolean cargoOuttakeRight() {
        return gamepad.getBumper(GenericHID.Hand.kRight);
    }

    @Override
    public boolean cargoOuttakeLeft() {
        return gamepad.getBumper(GenericHID.Hand.kLeft);
    }
}
