package frc.inputs;

import frc.utils.DriveSignal;

/**
 * Provides a skeleton for all of the functions that the driver's HID (human
 * interface device, e.g. a joystick or gamepad) must proivde.
 *
 * <p>
 * This allows for a very simple ability to switch which controllers are used
 * for controlling the robot, since the inputs are completely separated from the
 * robot's code.
 */
public interface DriverHid {
    DriveSignal getDriveSignal();

    boolean liftJack();

    boolean extendFrontJack();

    boolean retractFrontJack();

    boolean extendLeftJack();

    boolean retractLeftJack();

    boolean extendRightJack();

    boolean retractRightJack();

    DriveSignal runJackWheels();

    boolean holdAll();

    boolean cargoOuttakeRight();

    boolean cargoOuttakeLeft();
}