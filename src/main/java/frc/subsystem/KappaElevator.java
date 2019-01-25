package frc.subsystem;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.ControlType;
import edu.wpi.first.wpilibj.DigitalInput;

import static frc.utils.Constants.*;

public class KappaElevator extends Subsystem {
    private static final CANSparkMaxLowLevel.MotorType MOTOR_TYPE = CANSparkMaxLowLevel.MotorType.kBrushed;
    private static final double FEED_FORWARD_WITH_CARGO = 0.35;
    private static final double FEED_FORWARD_WITHOUT_CARGO = 0.30;
    private static KappaElevator instance;
    private final CANSparkMax left;
    private final CANSparkMax right;
    private ElevatorControlState state = ElevatorControlState.OPEN_LOOP;
    private PeriodicIO periodicIo = new PeriodicIO();
    private Cargo cargo = Cargo.getInstance();

    // TODO channel -> constant
    private DigitalInput bottomLimit = new DigitalInput(0);

    private KappaElevator() {
        left = new CANSparkMax(LEFT_LIFT_NEO, MOTOR_TYPE);
        right = new CANSparkMax(RIGHT_LIFT_NEO, MOTOR_TYPE);
        left.setIdleMode(CANSparkMax.IdleMode.kBrake);
        right.setIdleMode(CANSparkMax.IdleMode.kBrake);
        right.setInverted(true);
        right.follow(left);
        setOpenLoop(0.0);
    }

    public static KappaElevator getInstance() {
        if (instance == null) {
            instance = new KappaElevator();
        }
        return instance;
    }

    public synchronized void setOpenLoop(double power) {
        if (state != ElevatorControlState.OPEN_LOOP) {
            left.setIdleMode(CANSparkMax.IdleMode.kCoast);
            right.setIdleMode(CANSparkMax.IdleMode.kCoast);
            state = ElevatorControlState.OPEN_LOOP;
        }
        periodicIo.demand = power;
        periodicIo.feedForward = 0.0;
    }

    public synchronized void setPositionPid(double position) {
        if (state != ElevatorControlState.POSITION_PID) {
            left.setIdleMode(CANSparkMax.IdleMode.kBrake);
            right.setIdleMode(CANSparkMax.IdleMode.kBrake);
            state = ElevatorControlState.POSITION_PID;
        }
        periodicIo.demand = position;
    }

    @Override
    public void outputTelemetry() {
        ELEVATOR_SHUFFLEBOARD.putString("State", state.toString());
        ELEVATOR_SHUFFLEBOARD.putNumber("Demand", periodicIo.demand);
        ELEVATOR_SHUFFLEBOARD.putNumber("FeedForward", periodicIo.feedForward);
        ELEVATOR_SHUFFLEBOARD.putBoolean("Cargo In Hold", periodicIo.cargoInHold);
        ELEVATOR_SHUFFLEBOARD.putBoolean("Bottom Limit", periodicIo.bottomLimitEnabled);
    }

    @Override
    public synchronized void readPeriodicInputs() {
        periodicIo.bottomLimitEnabled = bottomLimit.get();
        periodicIo.cargoInHold = cargo.cargoInHold();
    }

    @Override
    public synchronized void writePeriodicOutputs() {
        periodicIo.feedForward = periodicIo.cargoInHold ? FEED_FORWARD_WITH_CARGO : FEED_FORWARD_WITHOUT_CARGO;
        ControlType controlType = state == ElevatorControlState.OPEN_LOOP ? ControlType.kDutyCycle : ControlType.kPosition;
        left.getPIDController().setReference(periodicIo.demand, controlType, 0, periodicIo.feedForward);
    }

    @Override
    public void stop() {
        setOpenLoop(0.0);
    }

    private enum ElevatorControlState {
        OPEN_LOOP,
        POSITION_PID
    }

    private static class PeriodicIO {
        // Inputs
        boolean bottomLimitEnabled;
        boolean cargoInHold;

        // Output
        double demand;
        double feedForward;
    }
}
