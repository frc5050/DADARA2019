package frc.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.DigitalInput;
import frc.utils.UnitConversions;

import static frc.utils.Constants.*;

@Deprecated
public class ElevatorBBot extends Subsystem {
    private static final double FEEDFORWARD_NO_CARGO = 0.3;
    private static final double FEEDFORWARD_WITH_CARGO = 0.3;
    private static final CANSparkMaxLowLevel.MotorType ELEVATOR_MOTOR_TYPE = CANSparkMaxLowLevel.MotorType.kBrushless;
    // TODO measure
    private static final double LOWER_LIMIT_DISTANCE_FROM_GROUND = UnitConversions.inchesToMeters(6.0);
    // TODO measure
    private static final double TOP_LIFT_HEIGHT = UnitConversions.inchesToMeters(63);

    // todo tune and make static final
    private double KP = 0.1;
    private double KI = 0.1;
    private double KD = 0.1;
    private double KI_ZONE = 0.1;
    private double MAX_OUTPUT = 0.3;
    private double MIN_OUTPUT = -0.3;
    private double DESIRED_ROTATIONS = 0.1;

    private static ElevatorBBot instance;

    // TODO do we need to add right side to anything else after slaving?
    private final WPI_TalonSRX left;
    private final VictorSPX right;

    private PeriodicIO periodicIo = new PeriodicIO();
    private DigitalInput bottom;
    private double offset = 0.0;

    private ElevatorBBot() {
        bottom = new DigitalInput(0);
        left = new WPI_TalonSRX(LEFT_LIFT_NEO);
        left.configFactoryDefault();
        left.configNominalOutputForward(0, 10);
        left.configNominalOutputReverse(0, 10);
        left.configPeakOutputForward(1.0, 10);
        left.configPeakOutputReverse(-1.0, 10);
        left.setSelectedSensorPosition(0, 0, 10);
        right = new VictorSPX(RIGHT_LIFT_NEO);
        right.setInverted(true);
        right.follow(left);

        // set PID coefficients
        left.config_kP(0, KP);
        left.config_kI(0, KI);
        left.config_kD(0, KD);
        left.config_kF(0, periodicIo.feedforward);

        // display PID coefficients on ELEVATOR_SHUFFLEBOARD
        ELEVATOR_SHUFFLEBOARD.putNumber("P Gain", KP);
        ELEVATOR_SHUFFLEBOARD.putNumber("I Gain", KI);
        ELEVATOR_SHUFFLEBOARD.putNumber("D Gain", KD);
        ELEVATOR_SHUFFLEBOARD.putNumber("I Zone", KI_ZONE);
        ELEVATOR_SHUFFLEBOARD.putNumber("Feed Forward", periodicIo.feedforward);
        ELEVATOR_SHUFFLEBOARD.putNumber("Max Output", MAX_OUTPUT);
        ELEVATOR_SHUFFLEBOARD.putNumber("Min Output", MIN_OUTPUT);
        ELEVATOR_SHUFFLEBOARD.putNumber("Set Rotations", DESIRED_ROTATIONS);

        // TODO check inversion and make sure that following works
        right.setInverted(true);
        right.follow(left);
    }

    private static class PeriodicIO {
        // Inputs
        double rawPosition;
        double velocity;
        double feedforward;

        // TODO implement me with query about having a ball
        boolean cargoHeld;
    }

    public static ElevatorBBot getInstance() {
        if (instance == null) {
            instance = new ElevatorBBot();
        }
        return instance;
    }

    @Override
    public synchronized void readPeriodicInputs() {
        periodicIo.rawPosition = left.get();
        periodicIo.velocity = left.getSelectedSensorVelocity();
        // TODO reimplement this with a branch based on whether we have cargo or not
        //  periodicIo.feedForward = FEEDFORWARD_NO_CARGO;

        // TODO remove when making tuning values const
        double p = ELEVATOR_SHUFFLEBOARD.getNumber("P Gain", 0);
        double i = ELEVATOR_SHUFFLEBOARD.getNumber("I Gain", 0);
        double d = ELEVATOR_SHUFFLEBOARD.getNumber("D Gain", 0);
        double iZone = ELEVATOR_SHUFFLEBOARD.getNumber("I Zone", 0);
        double f = ELEVATOR_SHUFFLEBOARD.getNumber("Feed Forward", 0);

        if (f != periodicIo.feedforward) {
            periodicIo.feedforward = f;
        }
        double maxOutput = ELEVATOR_SHUFFLEBOARD.getNumber("Max Output", 0);
        double minOutput = ELEVATOR_SHUFFLEBOARD.getNumber("Min Output", 0);
        double desiredRotations = ELEVATOR_SHUFFLEBOARD.getNumber("Set Rotations", 0);

        if (p != KP) KP = p;
        if (i != KP) KI = i;
        if (d != KD) KD = d;
        if (iZone != KI_ZONE) KI_ZONE = iZone;
        if (maxOutput != MAX_OUTPUT || minOutput != MIN_OUTPUT) {
            MAX_OUTPUT = maxOutput;
            MIN_OUTPUT = minOutput;
        }
        if (desiredRotations != DESIRED_ROTATIONS) DESIRED_ROTATIONS = desiredRotations;
    }

    @Override
    public synchronized void writePeriodicOutputs() {
//      TODO  left.set(ControlMode.MotionMagic, );
        left.configAllowableClosedloopError(0, 0);
    }

    @Override
    public void outputTelemetry() {
        // TODO(Raina)
        ELEVATOR_SHUFFLEBOARD.putNumber("Left Output", periodicIo.rawPosition);
        ELEVATOR_SHUFFLEBOARD.putNumber("Feed Forward", periodicIo.feedforward);
        ELEVATOR_SHUFFLEBOARD.putNumber("Velocity", periodicIo.velocity);
        ELEVATOR_SHUFFLEBOARD.putBoolean("Cargo Held", periodicIo.cargoHeld);
    }

    @Override
    public void stop() {
        left.stopMotor();
        right.set(ControlMode.PercentOutput, 0.0);
    }
}