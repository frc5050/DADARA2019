package frc.inputs;

/**
 * Provides a skeleton for all of the functions that the operator's HID (human
 * interface device, e.g. a joystick or gamepad) must proivde.
 *
 * <p>
 * This allows for a very simple ability to switch which controllers are used
 * for controlling the robot, since the inputs are completely separated from the
 * robot's code.
 */
public interface OperatorHid {
    // Cargo
    boolean cargoIntake();

    boolean cargoOuttakeFront();

    boolean cargoOuttakeRight();

    boolean cargoOuttakeLeft();

    // Elevator
    boolean setElevatorPositionLowCargo();

    boolean setElevatorPositionMidCargo();

    boolean setElevatorPositionHighCargo();

    boolean setElevatorPositionGroundHatch();

    boolean setElevatorPositionLowHatch();

    boolean setElevatorPositionMidHatch();

    boolean setElevatorPositionHighHatch();

    // Hatch
    // TODO(Raina) unless we have a student named Manuel on the team who will be moving the hatch, this should probably
    //  be spelled "Manual"
    double hatchManuel();

    boolean hatchRelease();
}