package frc.subsystem;

import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import frc.loops.Loop;
import frc.loops.LooperInterface;
import frc.utils.Constants;
import frc.utils.DriveSignal;
import frc.utils.UnitConversions;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Trajectory.FitMethod;

import static frc.utils.Constants.DRIVE_SHUFFLEBOARD;

public class Drive extends Subsystem {
    private static final double WHEEL_DIAMETER = UnitConversions.inchesToMeters(6.0);
    private static final double WHEEL_CIRCUMFERENCE = WHEEL_DIAMETER * Math.PI;
    private static final double PULSES_PER_REVOLUTION = 213;
    private static final double WHEELBASE_WIDTH = 0.56515;
    private static final double MAX_VELOCITY = 3.0;
    private static final double MAX_ACCELERATION = 70.0;
    private static final double MAX_JERK = 4000.0;
    private static final double DT = 0.01;
    private static final double FOLLOWER_KP = 1.0;
    private static final double FOLLOWER_KI = 0.0;
    private static final double FOLLOWER_KD = 0.0;
    private static final double FOLLOWER_KV = 1.0 / MAX_VELOCITY;
    private static final double FOLLOWER_KA = 0.0;
    private static final FitMethod FIT_METHOD = FitMethod.HERMITE_QUINTIC;
    private static final int SAMPLES = Trajectory.Config.SAMPLES_HIGH;
    private static final Trajectory.Config CONFIG = new Trajectory.Config(FIT_METHOD, SAMPLES, DT, MAX_VELOCITY,
            MAX_ACCELERATION, MAX_JERK);

    private static Drive instance;

    private final WPI_TalonSRX leftMotor;
    private final VictorSPX leftSlave;
    private final WPI_TalonSRX rightMotor;
    private final VictorSPX rightSlave;
    private State state = State.OPEN_LOOP;
    private PeriodicIO periodicIo = new PeriodicIO();
    private final Loop loop = new Loop() {
        @Override
        public void onStart(double timestamp) {
            synchronized (Drive.this) {
                setOpenLoop(DriveSignal.NEUTRAL);
            }
        }

        @Override
        public void onLoop(double timestamp) {
            // TODO
            // synchronized (DriveExp.this) {
            // switch (state) {
            // case OPEN_LOOP:
            // break;
            // case PATH_FOLLOWING:
            // // TODO implement paths back into this, but do it correctly time
            // // (i.e. closed loop velocity via PIDF (F = output of trajectory), goal =
            // // velocity from trajectory)
            // break;
            // default:
            // break;
            // }
            // }
        }

        @Override
        public void onStop(double timestamp) {
            stop();
        }
    };

    private Drive() {
        // TODO(Lucas) check inversion
        // TODO(Lucas) evaluate accuracy differences between velocity timings
        leftMotor = new WPI_TalonSRX(Constants.LEFT_DRIVE_1);
        leftMotor.setInverted(true);
        leftSlave = new VictorSPX(Constants.LEFT_DRIVE_2);
        leftSlave.follow(leftMotor);
        rightMotor = new WPI_TalonSRX(Constants.RIGHT_DRIVE_1);
        rightSlave = new VictorSPX(Constants.RIGHT_DRIVE_2);
        rightSlave.follow(rightMotor);
    }

    public static Drive getInstance() {
        if (instance == null) {
            instance = new Drive();
        }
        return instance;
    }

    public synchronized void setOpenLoop(DriveSignal driveSignal) {
        if (state != State.OPEN_LOOP) {
            state = State.OPEN_LOOP;
        }
        periodicIo.leftDemand = driveSignal.getLeftOutput();
        periodicIo.rightDemand = driveSignal.getRightOutput();
    }

    @Override
    public void outputTelemetry() {
        DRIVE_SHUFFLEBOARD.putString("Drive State", state.toString());
        DRIVE_SHUFFLEBOARD.putNumber("Left Demand", periodicIo.leftDemand);
        DRIVE_SHUFFLEBOARD.putNumber("Right Demand", periodicIo.rightDemand);
    }

    @Override
    public void stop() {
        setOpenLoop(DriveSignal.BRAKE);
    }

    @Override
    public void registerEnabledLoops(LooperInterface looper) {
        looper.registerLoop(loop);
    }

    @Override
    public synchronized void writePeriodicOutputs() {
        // TODO add in velocity control
        if (state == State.OPEN_LOOP) {
            leftMotor.set(periodicIo.leftDemand);
            rightMotor.set(periodicIo.rightDemand);
        } else {
            leftMotor.set(0.0);
            rightMotor.set(0.0);
        }
    }

    private enum State {
        OPEN_LOOP, PATH_FOLLOWING
    }

    public static class PeriodicIO {
        // TODO
        // Inputs
        double yaw;
        double roll;
        double pitch;

        // Outputs
        double leftDemand = 0.0;
        double rightDemand = 0.0;
    }
}