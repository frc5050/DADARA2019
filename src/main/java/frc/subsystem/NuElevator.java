package frc.subsystem;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.ControlType;
import edu.wpi.first.wpilibj.DigitalInput;
import frc.utils.BrakeMode;

import static frc.utils.Constants.*;

public class NuElevator extends Subsystem {
    private static final CANSparkMaxLowLevel.MotorType MOTOR_TYPE = CANSparkMaxLowLevel.MotorType.kBrushless;
    // TODO move these around/get values/fixme please
    private static final double FEED_FORWARD_WITH_CARGO = 0.35;
    private static final double FEED_FORWARD_WITHOUT_CARGO = 0.30;
    private static final double BOTTOM_DIST_FROM_GROUND = 0;
    private static final double DT = 0.01;
    private static final double ZEROING_VELOCITY = 0.02;
    private static final double SHAFT_DIAMETER = 1.732 * 0.0254;
    private static final double DISTANCE_PER_MOTOR_REVOLUTION = SHAFT_DIAMETER * Math.PI;
    private static final double ENCODER_OUTPUT_PER_REVOLUTION = 1.0;
    private static final double DISTANCE_PER_ENCODER_TICK = DISTANCE_PER_MOTOR_REVOLUTION / ENCODER_OUTPUT_PER_REVOLUTION;

    private static NuElevator instance;
    private final CANSparkMax left;
    private final CANSparkMax right;
    private Cargo cargo = Cargo.getInstance();

    // TODO channel -> constant
    private DigitalInput bottomLimit = new DigitalInput(0);
    private ElevatorState state = ElevatorState.OPEN_LOOP;
    private PeriodicIO periodicIo = new PeriodicIO();

    private NuElevator() {
        left = new CANSparkMax(LEFT_LIFT_NEO, MOTOR_TYPE);
        right = new CANSparkMax(RIGHT_LIFT_NEO, MOTOR_TYPE);
        left.setIdleMode(BrakeMode.BRAKE.getSparkMaxBrakeMode());
        right.setIdleMode(BrakeMode.BRAKE.getSparkMaxBrakeMode());
        right.setInverted(true);
        right.follow(left);
        stop();
    }

    public static NuElevator getInstance() {
        if (instance == null) {
            instance = new NuElevator();
        }
        return instance;
    }

    @Override
    public void stop() {
        setOpenLoop(0.0);
    }

    @Override
    public void writePeriodicOutputs() {
        // TODO limit switch should disable reverse movements

        if (state == ElevatorState.POSITION_CONTROLLED) {
            if (!periodicIo.hasZeroed) {
                periodicIo.offset = 0.0;
                periodicIo.demand = periodicIo.currentRawPosition - heightFromGroundToEncoder(ZEROING_VELOCITY * DT);
            } else {
                periodicIo.demand = heightFromGroundToEncoder(periodicIo.setPosition.getHeight());
                periodicIo.feedForward = periodicIo.cargoHeld ? FEED_FORWARD_WITH_CARGO : FEED_FORWARD_WITHOUT_CARGO;
            }
        }
        left.getPIDController().setReference(periodicIo.demand, periodicIo.controlType, 0, periodicIo.feedForward);
    }

    @Override
    public void readPeriodicInputs() {
        periodicIo.cargoHeld = cargo.cargoInHold();
        periodicIo.bottomLimitTriggered = bottomLimit.get();
        periodicIo.currentRawPosition = left.getEncoder().getPosition();

        if (periodicIo.bottomLimitTriggered) {
            // TODO should we only do this when we haven't zeroed?
            periodicIo.offset = -periodicIo.currentRawPosition;
            periodicIo.hasZeroed = true;
        }

        periodicIo.heightFromGround = encoderToHeightFromGround(periodicIo.currentRawPosition);
    }

    // TODO add zeroing method

    private synchronized double encoderToHeightFromGround(double encoder) {
        return ((encoder + periodicIo.offset) * DISTANCE_PER_ENCODER_TICK) + BOTTOM_DIST_FROM_GROUND;
    }

    private synchronized double heightFromGroundToEncoder(double heightFromGround) {
        return (heightFromGround - BOTTOM_DIST_FROM_GROUND) / DISTANCE_PER_ENCODER_TICK - periodicIo.offset;
    }

    public synchronized void setPosition(ElevatorPosition position) {
        checkAndConfigureNewState(ElevatorState.POSITION_CONTROLLED);
        periodicIo.setPosition = position;
    }

    public synchronized void setOpenLoop(double power) {
        checkAndConfigureNewState(ElevatorState.OPEN_LOOP);
        periodicIo.demand = power;
    }

    private synchronized void checkAndConfigureNewState(ElevatorState newState) {
        if (newState != state) {
            switch (newState) {
                case OPEN_LOOP:
                    periodicIo.demand = 0.0;
                    periodicIo.feedForward = 0.0;
                    periodicIo.controlType = ControlType.kDutyCycle;
                    break;
                case POSITION_CONTROLLED:
                    periodicIo.demand = 0.0;
                    periodicIo.feedForward = 0.0;
                    periodicIo.controlType = ControlType.kPosition;
                    break;
            }
            state = newState;
        }
    }

    @Override
    public void outputTelemetry() {
        ELEVATOR_SHUFFLEBOARD.putString("Desired Position", periodicIo.setPosition.toString());
        ELEVATOR_SHUFFLEBOARD.putNumber("Current Raw Position", periodicIo.currentRawPosition);
        ELEVATOR_SHUFFLEBOARD.putNumber("Height from Ground", periodicIo.heightFromGround);
        ELEVATOR_SHUFFLEBOARD.putNumber("Offset", periodicIo.offset);
        ELEVATOR_SHUFFLEBOARD.putBoolean("Cargo Held", periodicIo.cargoHeld);
        ELEVATOR_SHUFFLEBOARD.putBoolean("Bottom Limit Triggered", periodicIo.bottomLimitTriggered);
        ELEVATOR_SHUFFLEBOARD.putBoolean("Has Zeroed", periodicIo.hasZeroed);
        ELEVATOR_SHUFFLEBOARD.putString("ControlType", periodicIo.controlType.toString());
        ELEVATOR_SHUFFLEBOARD.putNumber("Demand", periodicIo.demand);
        ELEVATOR_SHUFFLEBOARD.putNumber("FeedForward", periodicIo.feedForward);
    }

    private enum ElevatorState {
        OPEN_LOOP,
        POSITION_CONTROLLED
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

    private static class PeriodicIO {
        // Inputs
        ElevatorPosition setPosition = ElevatorPosition.LOW_HATCH;
        double currentRawPosition;
        double heightFromGround;
        double offset;
        boolean cargoHeld;
        boolean bottomLimitTriggered;
        boolean hasZeroed;

        // Outputs
        ControlType controlType = ControlType.kDutyCycle;
        double demand;
        double feedForward;
    }
}
