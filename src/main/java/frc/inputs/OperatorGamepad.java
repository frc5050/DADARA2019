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
    private static final double TRIGGER_THRESHOLD = .9;
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

    private boolean invertLeftRight(){
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

  /**
   * Mid hatch position is set to the right trigger if it is greater than the trigger threshold.
   * @return
   */
    @Override
    public boolean setElevatorPositionMidHatch() {
        return operatorGamepad.getTriggerAxis(Hand.kRight) > TRIGGER_THRESHOLD;
    }

  /**
   * Sets the highHatch position to the left trigger if it is greater than the trigger threshold.
   * @return
   */
    @Override
    public boolean setElevatorPositionHighHatch() {
        return operatorGamepad.getTriggerAxis(Hand.kLeft) > TRIGGER_THRESHOLD;
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
        return operatorGamepad.getY(Hand.kRight);
    }

    @Override
    public boolean hatchRelease() {
        return operatorGamepad.getXButton();
    }

    @Override
    public double intakeTilt(){
      double rightTrigger = operatorGamepad.getTriggerAxis(Hand.kRight);
      if (rightTrigger > 0.03){
        return rightTrigger;
      } else {
        return -operatorGamepad.getTriggerAxis(Hand.kLeft);
      }

    }

}