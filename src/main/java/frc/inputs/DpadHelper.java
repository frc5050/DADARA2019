package frc.inputs;

import frc.inputs.LastDpadState;

import static frc.utils.Constants.*;

public class DpadHelper {
    public static LastDpadState lastDpadUpdate(LastDpadState lastDpadState, int pov) {
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
        return lastDpadState;
    }
}
