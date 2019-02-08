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
    private static OperatorGamepad instance;
    private final XboxController operatorGamepad;
    private boolean useCargoHeights = false;
    private LastDpadState lastDpadState = LastDpadState.NONE;
    private ElevatorHeight elevatorHeight = ElevatorHeight.NONE;

    /**
     * Constructor.
     */
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
        // XOR
        useCargoHeights = useCargoHeights ^ operatorGamepad.getXButtonPressed();

        if (operatorGamepad.getStickButton(Hand.kRight)) {
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
        return lastDpadState == LastDpadState.DOWN;
    }

    @Override
    public boolean cargoOuttakeFront() {
        return lastDpadState == LastDpadState.UP;
    }

    private boolean invertLeftRight() {
        return operatorGamepad.getRawButton(8);
    }

    @Override
    public boolean cargoIntakeRight() {
        int pov = operatorGamepad.getPOV();
        return (((pov == POV_DPAD_RIGHT) || (pov == POV_DPAD_LOWER_RIGHT || (pov == POV_DPAD_UPPER_RIGHT))) && operatorGamepad.getRawButton(8)) && invertLeftRight();
    }

    @Override
    public boolean cargoIntakeLeft() {
        int pov = operatorGamepad.getPOV();
        return ((pov == POV_DPAD_LEFT) || (pov == POV_DPAD_LOWER_LEFT || (pov == POV_DPAD_UPPER_LEFT))) && invertLeftRight();
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
    public boolean hatchFeederHeight() {
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

    /**
     * The last recorded value of the DPAD.
     *
     * <p>
     * Since the DPAD will quite often return a 45 degrees bisection between any of the four clickable directions, when
     * holding one side, jittering in and out of that value can happen, leading to jittering on the motors. By storing
     * the previously held state as one of the four valid directions, when the DPAD returns one of the 45 degree
     * bisections, the last state can be used to make an educated guess as to what is actually being held.
     */
    enum LastDpadState {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        NONE
    }

    /**
     * The different elevator heights. Used in combination with knowledge of whether to go to cargo or hatch heights to
     * return valid values for which height to set the elevator to.
     */
    enum ElevatorHeight {
        NONE,
        LOW,
        MID,
        HIGH
    }

}