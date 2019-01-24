package frc.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.ControlType;
import edu.wpi.first.wpilibj.DigitalInput;

import static frc.utils.Constants.*;

public class KappaElevatorTalonSRX extends Subsystem {
    private static final CANSparkMaxLowLevel.MotorType MOTOR_TYPE = CANSparkMaxLowLevel.MotorType.kBrushless;
    private static final double FEED_FORWARD_WITH_CARGO = 0.35;
    private static final double FEED_FORWARD_WITHOUT_CARGO = 0.30;
    private static KappaElevatorTalonSRX instance;
    private final WPI_TalonSRX left;
    private final VictorSPX right;
    private ElevatorControlState state = ElevatorControlState.OPEN_LOOP;
    private PeriodicIO periodicIo = new PeriodicIO();
//    private Cargo cargo = Cargo.getInstance();

    // TODO channel -> constant
    private DigitalInput bottomLimit = new DigitalInput(0);

    private KappaElevatorTalonSRX() {
        left = new WPI_TalonSRX(LEFT_LIFT_NEO);
        right = new VictorSPX(RIGHT_LIFT_NEO);
        left.setNeutralMode(NeutralMode.Brake);
        right.setNeutralMode(NeutralMode.Brake);
        right.setInverted(true);
        right.follow(left);
        setOpenLoop(0.0);
    }

    public static KappaElevatorTalonSRX getInstance() {
        if (instance == null) {
            instance = new KappaElevatorTalonSRX();
        }
        return instance;
    }

    public synchronized void setOpenLoop(double power) {
        if (state != ElevatorControlState.OPEN_LOOP) {
            left.setNeutralMode(NeutralMode.Coast);
            right.setNeutralMode(NeutralMode.Coast);
            state = ElevatorControlState.OPEN_LOOP;
        }
        periodicIo.demand = power;
        periodicIo.feedForward = 0.0;
    }

    public synchronized void setPositionPid(double position) {
        if (state != ElevatorControlState.POSITION_PID) {
            left.setNeutralMode(NeutralMode.Brake);
            right.setNeutralMode(NeutralMode.Brake);
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
//        periodicIo.cargoInHold = cargo.cargoInHold();
    }

    @Override
    public synchronized void writePeriodicOutputs() {
        periodicIo.feedForward = periodicIo.cargoInHold ? FEED_FORWARD_WITH_CARGO : FEED_FORWARD_WITHOUT_CARGO;
        ControlMode controlType = state == ElevatorControlState.OPEN_LOOP ? ControlMode.PercentOutput: ControlMode.Position;
        left.set(controlType, periodicIo.demand, DemandType.ArbitraryFeedForward, periodicIo.feedForward);
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
