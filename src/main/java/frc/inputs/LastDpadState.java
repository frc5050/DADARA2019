package frc.inputs;

/**
 * The last recorded value of the DPAD.
 *
 * <p>
 * Since the DPAD will quite often return a 45 degrees bisection between any of the four clickable directions, when
 * holding one side, jittering in and out of that value can happen, leading to jittering on the motors. By storing
 * the previously held state as one of the four valid directions, when the DPAD returns one of the 45 degree
 * bisections, the last state can be used to make an educated guess as to what is actually being held.
 */
public enum LastDpadState {
    UP,
    DOWN,
    LEFT,
    RIGHT,
    NONE
}
