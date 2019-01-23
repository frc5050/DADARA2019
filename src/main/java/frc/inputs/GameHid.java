package frc.inputs;

/**
 * Provides a skeleton for all of the functions that the all of the active
 * HID's, together, must provide. For most seasons, this essentially ties
 * together the driver and operator gamepad functions into one simpler
 * interface.
 *
 * <p>
 * This implementation provides a very simple and concise way to get all of the
 * data necessary for operating the robot.
 */
public interface GameHid extends DriverHid, OperatorHid {

}
