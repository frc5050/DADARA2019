package frc.states;

import com.ctre.phoenix.motorcontrol.ControlMode;

public class HatchState {
    // Input
    public boolean limitHit = false;
    public double encoder = 0.0;
    public double outputCurrent = 0.0;

    // Output
    public double demand = 0.0;
    public ControlMode controlMode = ControlMode.PercentOutput;
    public double peakOutputForward = 0.0;
    public double peakOutputReverse = 0.0;
    public boolean resetSensor = false;

    // Non-IO
    ControlState hatchState = ControlState.STOPPED;
    boolean hasZeroed = false;
    boolean hasHadHardwareFault = false;


    public enum ControlState {
        ZEROING(true),
        MOTION_MAGIC(true),
        OPEN_LOOP(false),
        STOPPED(false);

        private boolean requiresZeroing;

        ControlState(boolean requiresZeroing){
            this.requiresZeroing = requiresZeroing;
        }

        public boolean requiresZeroing(){
            return requiresZeroing;
        }
    }
}
