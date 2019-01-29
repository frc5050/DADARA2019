package frc.states;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.DriverStation;

import static frc.states.HatchState.ControlState.*;

public class HatchStateMachine {
    public static final double PEAK_FORWARD_OUTPUT_STANDARD = 1.0;
    public static final double PEAK_REVERSE_OUTPUT_STANDARD = -1.0;
    private static final double ENCODER_COUNTS_PER_REVOLUTION = 1024.0;
    private static final double REDUCTION_BETWEEN_ENCODER_AND_OUTPUT = 10.0;
    private static final double ROTATIONS_TO_ENCODER = (ENCODER_COUNTS_PER_REVOLUTION * REDUCTION_BETWEEN_ENCODER_AND_OUTPUT);
    private static final double ENCODER_TO_ROTATIONS = 1.0 / ROTATIONS_TO_ENCODER;
    private static final double PEAK_FORWARD_OUTPUT_LIMIT_PRESSED = 0.0;
    private static final double PEAK_REVERSE_OUTPUT_LIMIT_PRESSED = -1.0;
    private static final double HARDWARE_FAULT_AMPERAGE_MAXIMUM = 10.0;
    private static final int TOP_ENCODER_VALUE = 0;
    private static final int BOTTOM_ENCODER_VALUE = -4100;
    //TODO remove this mode probably
    private static final double RANGE = TOP_ENCODER_VALUE - BOTTOM_ENCODER_VALUE;
    private static final double MIDPOINT = ((double) (TOP_ENCODER_VALUE + BOTTOM_ENCODER_VALUE)) / 2.0;

    private HatchState systemState = new HatchState();
    private HatchState.ControlState desiredControlState = STOPPED;
    private int desiredEncoderPosition = 0;
    private double desiredOpenLoopPower = 0;
    private boolean limitHitOnLastUpdate = false;
    private double joystickPower = 0.0;

    public HatchStateMachine(double timestamp) {

    }

    private synchronized void zeroDesired() {
        this.desiredEncoderPosition = 0;
        this.desiredOpenLoopPower = 0.0;
    }

    public synchronized void setPosition(double joystickPower) {
//        zeroDesired();
        this.joystickPower = joystickPower;
        this.desiredEncoderPosition = (int) ((RANGE * joystickPower * 0.5) + MIDPOINT);
        this.desiredControlState = MOTION_MAGIC;
        // TODO filter
//        this.desiredEncoderPosition = desiredEncoderPosition;
    }

    public synchronized void setOpenLoop(double joystickPower) {
//        zeroDesired();
        this.joystickPower = joystickPower;
        this.desiredControlState = OPEN_LOOP;
        this.desiredOpenLoopPower = joystickPower;
    }

    public synchronized void zero() {
        systemState.hasZeroed = false;
        this.desiredControlState = ZEROING;
    }

    public synchronized HatchState update(HatchState currentState, double timestamp) {
        if (!systemState.hasHadHardwareFault && currentState.outputCurrent > HARDWARE_FAULT_AMPERAGE_MAXIMUM) {
            DriverStation.reportError(String.format("Hatch hardware fault detected with output current: %.4f", currentState.outputCurrent), false);
            systemState.hasHadHardwareFault = true;
        }

        if (!systemState.hasHadHardwareFault) {
            if (currentState.limitHit) {
                systemState.peakOutputForward = PEAK_FORWARD_OUTPUT_LIMIT_PRESSED;
                systemState.peakOutputReverse = PEAK_REVERSE_OUTPUT_LIMIT_PRESSED;
                if (!limitHitOnLastUpdate && !systemState.hasZeroed) {
                    systemState.resetSensor = true;
                    systemState.hasZeroed = true;
                }
            } else {
                systemState.resetSensor = false;
                systemState.peakOutputForward = PEAK_FORWARD_OUTPUT_STANDARD;
                systemState.peakOutputReverse = PEAK_REVERSE_OUTPUT_STANDARD;
            }

            limitHitOnLastUpdate = currentState.limitHit;

            // If we haven't zeroed, the current state requires zeroing, and we haven't hit the limit, zero the mechanism
            if (!systemState.hasZeroed && desiredControlState.requiresZeroing()) {
                systemState.hatchState = ZEROING;
            } else {
                systemState.hatchState = desiredControlState;
            }
        } else {
            systemState.hatchState = OPEN_LOOP;
            systemState.peakOutputForward = PEAK_FORWARD_OUTPUT_STANDARD;
            systemState.peakOutputReverse = PEAK_REVERSE_OUTPUT_STANDARD;
            systemState.resetSensor = false;
            systemState.hasZeroed = false;
        }

        handleUpdate();

        return systemState;
    }

    public synchronized boolean hasHadHardwareFault() {
        return systemState.hasHadHardwareFault;
    }

    public synchronized double getDesiredEncoder(){
        return desiredEncoderPosition;
    }

    private synchronized void handleUpdate() {
        switch (systemState.hatchState) {
            case ZEROING:
                systemState.demand = systemState.encoder + 30 /* + TODO */;
                systemState.controlMode = ControlMode.MotionMagic;
                break;
            case MOTION_MAGIC:
                systemState.demand = desiredEncoderPosition;
                systemState.controlMode = ControlMode.MotionMagic;
                break;
            case OPEN_LOOP:
                systemState.demand = joystickPower;
                systemState.controlMode = ControlMode.PercentOutput;
                break;
            case STOPPED:
                systemState.demand = 0.0;
                systemState.controlMode = ControlMode.PercentOutput;
                break;
        }
    }
}
