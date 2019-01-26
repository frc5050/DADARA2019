package frc.states;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.revrobotics.ControlType;
import com.revrobotics.CANDigitalInput.LimitSwitch;

public class ElevatorStateMachine {
    private static final double FEED_FORWARD_WITH_CARGO = 0.0;
    private static final double FEED_FORWARD_WITHOUT_CARGO = 0.0;
    private static final double BOTTOM_DIST_FROM_GROUND = 0;
    private static final double DT = 0.01;
    private static final double ZEROING_VELOCITY = 0.02;
    private static final double SHAFT_DIAMETER = 1.732 * 0.0254;
    private static final double DISTANCE_PER_MOTOR_REVOLUTION = SHAFT_DIAMETER * Math.PI; // 1.732 * 0.0254 * Math.PI / 1.0
    private static final double ENCODER_OUTPUT_PER_REVOLUTION = 1.0;
    private static final double DISTANCE_PER_ENCODER_TICK = DISTANCE_PER_MOTOR_REVOLUTION / ENCODER_OUTPUT_PER_REVOLUTION;
    private ElevatorState systemState = new ElevatorState();

    public ElevatorStateMachine() {

    }

    public ElevatorState update(ElevatorState update) {
        systemState.encoder = update.encoder;
        systemState.bottomLimitTouched = update.bottomLimitTouched;
        systemState.isCargoInHold = update.isCargoInHold;

        if (systemState.bottomLimitTouched) {
            systemState.offset = -systemState.encoder;
        }

        systemState.encoderFiltered = systemState.encoder + systemState.offset;
        systemState.distFromGround = getHeightFromGround();

        if(systemState.bottomLimitTouched) {
            systemState.minimumOutput = 0;
            systemState.maximumOutput = 1;
        } else {
            systemState.minimumOutput = -1;
            systemState.maximumOutput = 1;
        }

        switch (systemState.state) {
            case OPEN_LOOP:
                systemState.demand = systemState.desiredOpenLoopPercentage;
                systemState.feedforward = 0.0;
                break;
            case POSITION_PID:
                systemState.demand = heightFromGroundToEncoder(systemState.desiredPosition.getHeight());
                systemState.feedforward = systemState.isCargoInHold ? FEED_FORWARD_WITH_CARGO : FEED_FORWARD_WITHOUT_CARGO;
                break;
        }
        return systemState;
    }

    public void setOpenLoop(double percentage) {
        if (systemState.state != ElevatorControlMode.OPEN_LOOP) {
            systemState.state = ElevatorControlMode.OPEN_LOOP;
            systemState.feedforward = 0.0;
            systemState.controlType = ControlType.kDutyCycle;
        }
        systemState.desiredOpenLoopPercentage = percentage;
    }

    public void setPosition(ElevatorPosition position) {
        if (systemState.state != ElevatorControlMode.POSITION_PID) {
            systemState.state = ElevatorControlMode.POSITION_PID;
            systemState.feedforward = 0.0;
            systemState.controlType = ControlType.kPosition;
        }
        systemState.desiredPosition = position;
    }

    private double getEncoderFiltered() {
        return systemState.encoderFiltered;
    }

    private double getHeightFromGround() {
        return (systemState.encoderFiltered * DISTANCE_PER_ENCODER_TICK) + BOTTOM_DIST_FROM_GROUND;
    }

    private double heightFromGroundToEncoder(double heightFromGround) {
        return (heightFromGround - BOTTOM_DIST_FROM_GROUND) / DISTANCE_PER_ENCODER_TICK - systemState.offset;
    }

    public enum ElevatorControlMode {
        OPEN_LOOP,
        POSITION_PID
    }

    public enum ElevatorPosition {
        LOW_HATCH(0.0254),
        MID_HATCH(0.508),
        HIGH_HATCH(0.762),
        LOW_CARGO(0.381),
        MID_CARGO(0.635),
        HIGH_CARGO(0.889);

        private double height;

        ElevatorPosition(double height) {
            this.height = height;
        }

        public double getHeight() {
            return height;
        }
    }

    public static class ElevatorState {
        // Input
        double encoder;
        boolean bottomLimitTouched;
        boolean isCargoInHold;
        double encoderFiltered;
        double distFromGround;
        double offset;
        double desiredOpenLoopPercentage;
        ElevatorPosition desiredPosition;
        ElevatorControlMode state = ElevatorControlMode.OPEN_LOOP;
        double maximumOutput;
        double minimumOutput;

        // Output

        double demand;
        double feedforward;
        ControlType controlType = ControlType.kDutyCycle;
    }
}
