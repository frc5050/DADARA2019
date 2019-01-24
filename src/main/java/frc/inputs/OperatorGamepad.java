package frc.inputs;

import edu.wpi.first.wpilibj.XboxController;
import frc.utils.Constants;

/**
 * An implementation of the Driver's controls for when the robot is being
 * operated with a gamepad.
 */
public class OperatorGamepad implements OperatorHid {

    private static final int POV_DPAD_UP = 0;
    private static final int POV_DPAD_RIGHT = 90;
    private static final int POV_DPAD_DOWN = 180;
    private static final int POV_DPAD_LEFT = 270;
    private static OperatorGamepad instance;
    private final XboxController operatorGamepad;

    private OperatorGamepad() {
        operatorGamepad = new XboxController(Constants.OPERATOR_GAMEPAD_PORT);
    }

    public static OperatorGamepad getInstance() {
        if (instance == null) {
            instance = new OperatorGamepad();
        }
        return instance;
    }

    @Override
    public boolean cargoIntake() {
        return operatorGamepad.getPOV() == POV_DPAD_DOWN;
    }

    @Override
    public boolean cargoOuttakeFront() {
        return operatorGamepad.getPOV() == POV_DPAD_UP;
    }

    @Override
    public boolean cargoOuttakeRight() {
        return operatorGamepad.getPOV() == POV_DPAD_RIGHT;
    }

    @Override
    public boolean cargoOuttakeLeft() {
        return operatorGamepad.getPOV() == POV_DPAD_LEFT;
    }

    @Override
    public boolean setElevatorPositionLowCargo() {
        // TODO(Raina)
        return false;
    }

    @Override
    public boolean setElevatorPositionMidCargo() {
        // TODO(Raina)
        return false;
    }

    @Override
    public boolean setElevatorPositionHighCargo() {
        // TODO(Raina)
        return false;
    }

    @Override
    public boolean setElevatorPositionGroundHatch() {
        // TODO(Raina)
        return false;
    }

    @Override
    public boolean setElevatorPositionLowHatch() {
        // TODO(Raina)
        return false;
    }

    @Override
    public boolean setElevatorPositionMidHatch() {
        // TODO(Raina)
        return false;
    }

    @Override
    public boolean setElevatorPositionHighHatch() {
        // TODO(Raina)
        return false;
    }

    @Override
    public double hatchManual() {
        // TODO(Raina)
        return 0;
    }

    @Override
    public boolean hatchRelease() {
        // TODO(Raina)
        return false;
    }


}