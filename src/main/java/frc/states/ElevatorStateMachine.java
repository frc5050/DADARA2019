package frc.states;

import com.revrobotics.ControlType;

public class ElevatorStateMachine {
    public static final double FEED_FORWARD_WITH_CARGO = 0.0;
    public static final double FEED_FORWARD_WITHOUT_CARGO = 0.0;
    public static final double BOTTOM_DIST_FROM_GROUND = 0;
    public static final double ZEROING_VELOCITY = 0.02;
    public static final double SHAFT_DIAMETER = 1.732 * 0.0254;
    public static final double DISTANCE_PER_MOTOR_REVOLUTION = SHAFT_DIAMETER * Math.PI; // 1.732 * 0.0254 * Math.PI / 1.0
    public static final double ENCODER_OUTPUT_PER_REVOLUTION = 1.0;
    public static final double DISTANCE_PER_ENCODER_TICK = DISTANCE_PER_MOTOR_REVOLUTION / ENCODER_OUTPUT_PER_REVOLUTION;
    public static final double MAXIMUM_OUTPUT_STANDARD = 1.0;
    public static final double MINIMUM_OUTPUT_STANDARD = -1.0;
    public static final double MINIMUM_OUTPUT_BOTTOM_LIMIT = 0.0;
    private ElevatorState systemState = new ElevatorState();

    public ElevatorStateMachine() {

    }

    public synchronized ElevatorState update(ElevatorState update) {
        systemState.encoder = update.encoder;
        systemState.bottomLimitTouched = update.bottomLimitTouched;
        systemState.isCargoInHold = update.isCargoInHold;

        if (systemState.bottomLimitTouched) {
            systemState.offset = -systemState.encoder;
        }

        systemState.encoderFiltered = systemState.encoder + systemState.offset;
        systemState.heightFromGround = getHeightFromGround();

        if (systemState.bottomLimitTouched) {
            systemState.maximumOutput = MAXIMUM_OUTPUT_STANDARD;
            systemState.minimumOutput = MINIMUM_OUTPUT_BOTTOM_LIMIT;
        } else {
            systemState.maximumOutput = MAXIMUM_OUTPUT_STANDARD;
            systemState.minimumOutput = MINIMUM_OUTPUT_STANDARD;
        }

        switch (systemState.state) {
            case OPEN_LOOP:
                systemState.demand = systemState.desiredOpenLoopPercentage;
                // TODO we could do feedforward and make manual control a little bit smoother
                systemState.feedforward = 0.0;
                break;
            case POSITION_PID:
                systemState.demand = heightFromGroundToEncoder(systemState.desiredPosition.getHeight());
                systemState.feedforward = systemState.isCargoInHold ? FEED_FORWARD_WITH_CARGO : FEED_FORWARD_WITHOUT_CARGO;
                break;
            case ZEROING:
                systemState.demand = heightFromGroundToEncoder(systemState.heightFromGround - (ZEROING_VELOCITY * 0.01));
                systemState.feedforward = systemState.isCargoInHold ? FEED_FORWARD_WITH_CARGO : FEED_FORWARD_WITHOUT_CARGO;
                break;
        }
        return systemState;
    }

    public synchronized void setOpenLoop(double percentage) {
        if (systemState.state != ElevatorControlMode.OPEN_LOOP) {
            systemState.state = ElevatorControlMode.OPEN_LOOP;
            systemState.feedforward = 0.0;
            systemState.controlType = ControlType.kDutyCycle;
        }
        systemState.desiredOpenLoopPercentage = percentage;
    }

    public synchronized void setZeroing(){
        if(systemState.state != ElevatorControlMode.ZEROING){
            systemState.state = ElevatorControlMode.ZEROING;
            systemState.feedforward = 0.0;
            systemState.controlType = ControlType.kPosition;
        }
    }

    public synchronized void setPosition(ElevatorPosition position) {
        if (systemState.state != ElevatorControlMode.POSITION_PID) {
            systemState.state = ElevatorControlMode.POSITION_PID;
            systemState.feedforward = 0.0;
            systemState.controlType = ControlType.kPosition;
        }
        systemState.desiredPosition = position;
    }

    private synchronized double getEncoderFiltered() {
        return systemState.encoderFiltered;
    }

    private synchronized double getHeightFromGround() {
        return (systemState.encoderFiltered * DISTANCE_PER_ENCODER_TICK) + BOTTOM_DIST_FROM_GROUND;
    }

    public synchronized double heightFromGroundToEncoder(double heightFromGround) {
        return (heightFromGround - BOTTOM_DIST_FROM_GROUND) / DISTANCE_PER_ENCODER_TICK - systemState.offset;
    }

    public synchronized double encoderToHeightFromGround(double encoder) {
        return (encoder + systemState.offset) * DISTANCE_PER_ENCODER_TICK + BOTTOM_DIST_FROM_GROUND;
    }

    public enum ElevatorControlMode {
        OPEN_LOOP,
        POSITION_PID,
        ZEROING
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
        public double encoder;
        public boolean bottomLimitTouched;
        public boolean isCargoInHold;
        public double maximumOutput;
        public double minimumOutput;
        public double demand;
        public double feedforward;
        public ControlType controlType = ControlType.kDutyCycle;
        double encoderFiltered;
        double heightFromGround;
        double offset;

        // Output
        double desiredOpenLoopPercentage;
        ElevatorPosition desiredPosition;
        ElevatorControlMode state = ElevatorControlMode.OPEN_LOOP;
    }
}
