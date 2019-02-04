package frc.inputs;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.XboxController;
import frc.utils.Constants;

/**
 * An implementation of the Driver's controls for when the robot is being
 * operated with a gamepad.
 */
public class OperatorGamepad implements OperatorHid {
    private static final int POV_DPAD_UP = 0;
    private static final int POV_DPAD_UPPER_RIGHT = 45;
    private static final int POV_DPAD_LOWER_RIGHT = 135;
    private static final int POV_DPAD_RIGHT = 90;
    private static final int POV_DPAD_DOWN = 180;
    private static final int POV_DPAD_LEFT = 270;
    private static final int POV_DPAD_UPPER_LEFT = 315;
    private static final int POV_DPAD_LOWER_LEFT = 225;
    private static final double TRIGGER_THRESHOLD = .9;
    private static OperatorGamepad instance;
    private final XboxController operatorGamepad;
    private boolean useCargoHeights = false;
    private LastDpadState lastDpadState = LastDpadState.NONE;
    private ElevatorHeight elevatorHeight = ElevatorHeight.NONE;

    private OperatorGamepad() {
        operatorGamepad = new XboxController(Constants.OPERATOR_GAMEPAD_PORT);
    }

    public static OperatorGamepad getInstance() {
        if (instance == null) {
            instance = new OperatorGamepad();
        }
        return instance;
    }

    public void update() {
        int pov = operatorGamepad.getPOV();
        if (pov == -1) {
            lastDpadState = LastDpadState.NONE;
        } else if (pov == POV_DPAD_DOWN) {
            lastDpadState = LastDpadState.DOWN;
        } else if (pov == POV_DPAD_RIGHT) {
            lastDpadState = LastDpadState.RIGHT;
        } else if (pov == POV_DPAD_LEFT) {
            lastDpadState = LastDpadState.LEFT;
        } else if (pov == POV_DPAD_UP) {
            lastDpadState = LastDpadState.UP;
        } else if (pov == POV_DPAD_UPPER_RIGHT) {
            lastDpadState = lastDpadState == LastDpadState.RIGHT ? LastDpadState.RIGHT : (lastDpadState == LastDpadState.UP ? LastDpadState.UP : LastDpadState.NONE);
        } else if (pov == POV_DPAD_LOWER_RIGHT) {
            lastDpadState = lastDpadState == LastDpadState.RIGHT ? LastDpadState.RIGHT : (lastDpadState == LastDpadState.DOWN ? LastDpadState.DOWN : LastDpadState.NONE);
        } else if (pov == POV_DPAD_UPPER_LEFT) {
            lastDpadState = lastDpadState == LastDpadState.LEFT ? LastDpadState.LEFT : (lastDpadState == LastDpadState.UP ? LastDpadState.UP : LastDpadState.NONE);
        } else if (pov == POV_DPAD_LOWER_LEFT) {
            lastDpadState = lastDpadState == LastDpadState.LEFT ? LastDpadState.LEFT : (lastDpadState == LastDpadState.DOWN ? LastDpadState.DOWN : LastDpadState.NONE);
        }
        useCargoHeights = useCargoHeights ^ operatorGamepad.getXButtonPressed();

        if (operatorGamepad.getStickButton(Hand.kLeft)) {
            elevatorHeight = ElevatorHeight.NONE;
        } else if (operatorGamepad.getAButtonPressed()) {
            elevatorHeight = ElevatorHeight.LOW;
        } else if (operatorGamepad.getBButtonPressed()) {
            elevatorHeight = ElevatorHeight.MID;
        } else if (operatorGamepad.getYButtonPressed()) {
            elevatorHeight = ElevatorHeight.HIGH;
        }
    }

    @Override
    public boolean cargoIntake() {
//        return operatorGamepad.getPOV() == POV_DPAD_DOWN;
        return lastDpadState == LastDpadState.DOWN;
    }

    @Override
    public boolean cargoOuttakeFront() {
//        return operatorGamepad.getPOV() == POV_DPAD_UP;
        return lastDpadState == LastDpadState.UP;
    }

    @Override
    public boolean cargoOuttakeRight() {
//        int pov = operatorGamepad.getPOV();
//        return (pov == POV_DPAD_RIGHT) || (pov == POV_DPAD_LOWER_RIGHT || (pov == POV_DPAD_UPPER_RIGHT));
        return lastDpadState == LastDpadState.RIGHT;
    }

    @Override
    public boolean cargoOuttakeLeft() {
//        int pov = operatorGamepad.getPOV();
//        return (pov == POV_DPAD_LEFT) || (pov == POV_DPAD_LOWER_LEFT || (pov == POV_DPAD_UPPER_LEFT));
        return lastDpadState == LastDpadState.LEFT;
    }

    private boolean invertLeftRight() {
        return operatorGamepad.getRawButton(8);
    }

    @Override
    public boolean cargoIntakeRight() {
        return cargoOuttakeRight() && invertLeftRight();
    }

    @Override
    public boolean cargoIntakeLeft() {
        return cargoOuttakeLeft() && invertLeftRight();
    }

    @Override
    public boolean setElevatorPositionLowCargo() {
        return elevatorHeight == ElevatorHeight.LOW && useCargoHeights;
    }

    @Override
    public boolean setElevatorPositionMidCargo() {
        return elevatorHeight == ElevatorHeight.MID && useCargoHeights;
    }

    @Override
    public boolean setElevatorPositionHighCargo() {
        return elevatorHeight == ElevatorHeight.HIGH && useCargoHeights;
    }

    @Override
    public boolean setElevatorPositionLowHatch() {
        return elevatorHeight == ElevatorHeight.LOW && !useCargoHeights;
    }

    @Override
    public boolean setElevatorPositionMidHatch() {
        return elevatorHeight == ElevatorHeight.MID && !useCargoHeights;
    }

    /**
     * Sets the highHatch position to the left trigger if it is greater than the trigger threshold.
     *
     * @return
     */
    @Override
    public boolean setElevatorPositionHighHatch() {
        return elevatorHeight == ElevatorHeight.HIGH && !useCargoHeights;
    }

    @Override
    public double hatchManual() {
        return -operatorGamepad.getY(Hand.kLeft);
    }

    @Override
    public boolean useHatchOpenLoop() {
        return operatorGamepad.getRawButton(7);
    }

    @Override
    public double elevateManual() {
        return -operatorGamepad.getY(Hand.kRight);
    }

    @Override
    public boolean hatchRelease() {
        return operatorGamepad.getXButton();
    }

    @Override
    public double intakeTilt() {
        double rightTrigger = operatorGamepad.getTriggerAxis(Hand.kRight);
        if (rightTrigger > 0.03) {
            return rightTrigger;
        } else {
            return -operatorGamepad.getTriggerAxis(Hand.kLeft);
        }

    }

    enum LastDpadState {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        NONE
    }

    enum ElevatorHeight {
        NONE,
        LOW,
        MID,
        HIGH
    }

}