package frc.inputs;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
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
    private static final double trigger_Threshold = .9;
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
        return operatorGamepad.getAButton();
    }

    @Override
    public boolean setElevatorPositionMidCargo() {
        return operatorGamepad.getBButton();
    }

    @Override
    public boolean setElevatorPositionHighCargo() {
        return operatorGamepad.getYButton();
    }

    @Override
    public boolean setElevatorPositionGroundHatch() {
        return operatorGamepad.getBumper(Hand.kRight);
    }

    @Override
    public boolean setElevatorPositionLowHatch() {
        return operatorGamepad.getBumper(Hand.kLeft);
    }

    @Override
    public boolean setElevatorPositionMidHatch() {
        return operatorGamepad.getTriggerAxis(Hand.kRight) > trigger_Threshold;
    }

    @Override
    public boolean setElevatorPositionHighHatch() {
        return operatorGamepad.getTriggerAxis(Hand.kLeft) > trigger_Threshold;
    }

    @Override
    public double hatchManual() {
        return operatorGamepad.getY(Hand.kLeft);
    }

    @Override
    public double elevateManual() {
        return operatorGamepad.getY(Hand.kRight);
    }

    @Override
    public boolean hatchRelease() {
        return operatorGamepad.getXButton();
    }


}