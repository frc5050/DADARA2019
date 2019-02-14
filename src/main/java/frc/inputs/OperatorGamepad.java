package frc.inputs;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.XboxController;
import frc.utils.Constants;
import frc.utils.DpadHelper;

import static frc.utils.DpadHelper.LastDpadState;

/**
 * Provides an implementation of an {@link GameHid} to encapsulate all of the functions that the all of the active
 * HID's, together, must provide. For most seasons, this essentially ties
 * together the driver and operator gamepad functions into one simpler
 * interface.
 */
public class OperatorGamepad implements OperatorHid {
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

    /**
     * Returns a static instance of the {@link OperatorGamepad} class. If none has been created yet, the instance
     * is created. This enables multiple any other classes to use this class without having to pass an instance or take
     * the risk of trying to instantiate multiple instances of this class, which would result in errors.
     *
     * @return a static instance of the {@link OperatorGamepad} subsystem.
     */
    public static OperatorGamepad getInstance() {
        if (instance == null) {
            instance = new OperatorGamepad();
        }
        return instance;
    }

    @Override
    public void update() {
        int pov = operatorGamepad.getPOV();
        lastDpadState = DpadHelper.lastDpadUpdate(lastDpadState, pov);

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
    public void disabled() {
        lastDpadState = LastDpadState.NONE;
        useCargoHeights = false;
        elevatorHeight = ElevatorHeight.NONE;
    }

    @Override
    public void disabledPeriodic() {
        operatorGamepad.getAButtonPressed();
        operatorGamepad.getBButtonPressed();
        operatorGamepad.getYButtonPressed();
        operatorGamepad.getXButtonPressed();
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
    public boolean placeHatch() {
        return operatorGamepad.getBumper(Hand.kRight);
    }

    @Override
    public boolean pullHatch() {
        return operatorGamepad.getBumper(Hand.kLeft);
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