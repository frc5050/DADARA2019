package frc.inputs;

import frc.robot.Robot;

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

    /**
     * Updates internal variables in the controller. Must be called every loop in {@link Robot#teleopPeriodic()}.
     */
    void update();

    /**
     * Clears internal variables in the controller. Must be called on {@link Robot#disabledInit()}.
     */
    void disabled();

    /**
     * Clears internal variables in the controller. Must be called every loop in {@link Robot#disabledPeriodic()}.
     */
    void disabledPeriodic();

    /**
     * Returns true if the {@link frc.subsystem.Cargo} subsystem should intake a cargo from the back.
     *
     * @return true if the {@link frc.subsystem.Cargo} subsystem should intake cargo from the back.
     */
    boolean cargoIntake();

    /**
     * Returns true if the {@link frc.subsystem.Cargo} subsystem should outtake cargo to the front.
     *
     * @return true if the {@link frc.subsystem.Cargo} subsystem should outtake cargo to the front.
     */
    boolean cargoOuttakeFront();

    /**
     * Returns the value to issue to the intake tilt motor.
     *
     * @return the value on the range [-1.0, 1.0] to issue to the intake tilt motor, with positive values corresponding to
     * an upwards tilt and negative corresponding with downwards.
     */
    double intakeTilt();

    // Elevator

    /**
     * Return true if the {@link frc.subsystem.Elevator} should set its height to the low cargo position, false
     * otherwise.
     *
     * @return true if the {@link frc.subsystem.Elevator} should set its height to the low cargo position, false
     * otherwise.
     */
    boolean setElevatorPositionLowCargo();

    /**
     * Return true if the {@link frc.subsystem.Elevator} should set its height to the middle cargo position, false
     * otherwise.
     *
     * @return true if the {@link frc.subsystem.Elevator} should set its height to the middle cargo position, false
     * otherwise.
     */
    boolean setElevatorPositionMidCargo();

    /**
     * Return true if the {@link frc.subsystem.Elevator} should set its height to the high cargo position, false
     * otherwise.
     *
     * @return true if the {@link frc.subsystem.Elevator} should set its height to the high cargo position, false
     * otherwise.
     */
    boolean setElevatorPositionHighCargo();

    /**
     * Return true if the {@link frc.subsystem.Elevator} should set its height to the low hatch position, false
     * otherwise.
     *
     * @return true if the {@link frc.subsystem.Elevator} should set its height to the low hatch position, false
     * otherwise.
     */
    boolean setElevatorPositionLowHatch();

    /**
     * Return true if the {@link frc.subsystem.Elevator} should set its height to the middle hatch position, false
     * otherwise.
     *
     * @return true if the {@link frc.subsystem.Elevator} should set its height to the middle hatch position, false
     * otherwise.
     */
    boolean setElevatorPositionMidHatch();


    /**
     * Return true if the {@link frc.subsystem.Elevator} should set its height to the high hatch position, false
     * otherwise.
     *
     * @return true if the {@link frc.subsystem.Elevator} should set its height to the high hatch position, false
     * otherwise.
     */
    boolean setElevatorPositionHighHatch();

    /**
     * Returns the value to issue to the hatch mechanism while it is being manually controlled.
     *
     * @return a value in the range [-1.0, 1.0] to be issued to the hatch mechanism.
     */
    double hatchManual();

    /**
     * Returns true if the hatch should be run in open loop rather than closed loop mode.
     *
     * @return true if the hatch should be run in open loop rather than closed loop mode.
     */
    boolean useHatchOpenLoop();

    /**
     * Returns the value to issue to the elevator motor when it is controlled manually.
     *
     * @return a value in the range [-1.0, 1.0] to be issued to the elevator when controlled manually.
     */
    double elevateManual();

    /**
     * Returns true when the hatch should be set to the correct position in order to correctly grab from the feeder
     * station.
     *
     * @return true when the hatch should be set to the correct position in order to correctly grab from the feeder
     * station.
     */
    boolean hatchFeederHeight();
}