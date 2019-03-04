package frc.inputs;

import frc.robot.Robot;
import frc.utils.DriveSignal;

/**
 * Provides a skeleton for all of the functions that the driver's HID (human
 * interface device, e.g. a joystick or gamepad) must provide.
 *
 * <p>
 * This allows for a very simple ability to switch which controllers are used
 * for controlling the robot, since the inputs are completely separated from the
 * robot's code.
 */
interface DriverHid {
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
     * Returns the {@link DriveSignal} to issue to the drive base for manual control.
     *
     * @return the {@link DriveSignal} to issue to the drive base for manual control
     */
    DriveSignal getDriveSignal();

    /**
     * Returns true if the {@link frc.subsystem.Jacks} should initiate the lifting procedure, false otherwise.
     *
     * @return true if the {@link frc.subsystem.Jacks} should initiate the lifting procedure, false otherwise.
     */
    boolean liftAllJacks();

    /**
     * Returns true if the {@link frc.subsystem.Jacks} should initiate the retraction procedure, false otherwise.
     *
     * @return true if the {@link frc.subsystem.Jacks} should initiate the retraction procedure, false otherwise.
     */
    boolean retractAllJacks();

    /**
     * Returns true if the {@link frc.subsystem.Jacks} should initiate the hab climbing procedure, false otherwise.
     *
     * @return true if the {@link frc.subsystem.Jacks} should initiate the hab climbing procedure, false otherwise.
     */
    boolean initializeHabClimbingLevel3();

    boolean initializeHabClimbingLevel2();

    /**
     * Returns true if manual control should override wheel control on the wheels on the rear
     * jacks in the {@link frc.subsystem.Jacks} subsystem..
     *
     * @return true if manual control should override wheel control on the jack wheels, false otherwise.
     */
    boolean manualJackOverride();

    /**
     * Returns true if the {@link frc.subsystem.Jacks} should initiate the zeroing procedure, false otherwise.
     *
     * @return true if the {@link frc.subsystem.Jacks} should initiate the zeroing procedure, false otherwise.
     */
    boolean zeroJacks();

    /**
     * Returns the {@link DriveSignal} to issue to the jack wheels for manual control.
     *
     * @return the {@link DriveSignal} to issue to the jack wheels for manual control
     */
    DriveSignal runJackWheels();

    /**
     * Returns true if the {@link frc.subsystem.Cargo} subsystem should outtake cargo to the right.
     *
     * @return true if the {@link frc.subsystem.Cargo} subsystem should outtake cargo to the right.
     */
    boolean cargoOuttakeRight();

    /**
     * Returns true if the {@link frc.subsystem.Cargo} subsystem should outtake cargo to the left.
     *
     * @return true if the {@link frc.subsystem.Cargo} subsystem should outtake cargo to the left.
     */
    boolean cargoOuttakeLeft();

    /**
     * Returns true if the {@link frc.subsystem.Cargo} subsystem should intake cargo from the right.
     *
     * @return true if the {@link frc.subsystem.Cargo} subsystem should intake cargo from the right.
     */
    boolean cargoIntakeRight();

    /**
     * Returns true if the {@link frc.subsystem.Cargo} subsystem should intake cargo from the left.
     *
     * @return true if the {@link frc.subsystem.Cargo} subsystem should intake cargo from the left.
     */
    boolean cargoIntakeLeft();
}