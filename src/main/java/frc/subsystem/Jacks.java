package frc.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import frc.utils.Constants;
import frc.utils.DriveSignal;

import static frc.utils.Constants.JACKS_SHUFFLEBOARD;

public class Jacks extends Subsystem {

    // TODO(Lucas) use pidf + navx to control ratios while maximizing speed
    private final static double LEFT_REAR_LIFT_MULTIPLIER = 0.75 / 0.85;
    private final static double RIGHT_REAR_LIFT_MULTIPLIER = 0.65 / 0.85;
    private final static double FRONT_LIFT_MULTIPLIER = 1.0;
    private final static double DEFAULT_JACK_POWER = 0.5;
    private static Jacks instance;
    private final WPI_TalonSRX leftRearWheel;
    private final WPI_TalonSRX rightRearWheel;
    private final WPI_TalonSRX leftRearJack;
    private final WPI_TalonSRX rightRearJack;
    private final WPI_TalonSRX frontJack;
    private RobotState robotState = RobotState.getInstance();
    private PeriodicIO periodicIo = new PeriodicIO();

    private Jacks() {
        leftRearWheel = new WPI_TalonSRX(Constants.LEFT_REAR_JACK_WHEEL);
        rightRearWheel = new WPI_TalonSRX(Constants.RIGHT_REAR_JACK_WHEEL);
        leftRearJack = new WPI_TalonSRX(Constants.LEFT_REAR_JACK_LIFT);
        rightRearJack = new WPI_TalonSRX(Constants.RIGHT_REAR_JACK_LIFT);
        frontJack = new WPI_TalonSRX(Constants.FRONT_JACK_LIFT);
        // TODO test the jack inversion
        frontJack.setInverted(true);
        // TODO test the wheel inversion
        leftRearWheel.setInverted(true);
    }

    public static Jacks getInstance() {
        if (instance == null) {
            instance = new Jacks();
        }
        return instance;
    }

    @Override
    public void outputTelemetry() {
        JACKS_SHUFFLEBOARD.putNumber("Jack Left Rear Wheel Output", periodicIo.leftRearWheelOutput);
        JACKS_SHUFFLEBOARD.putNumber("Jack Right Rear Wheel Output", periodicIo.rightRearWheelOutput);
        JACKS_SHUFFLEBOARD.putNumber("Jack Left Rear Jack Output", periodicIo.leftRearJackOutput);
        JACKS_SHUFFLEBOARD.putNumber("Jack Right Rear Jack Output", periodicIo.rightRearJackOutput);
        JACKS_SHUFFLEBOARD.putNumber("Jack Front Rear Jack Output", periodicIo.frontJackOutput);
    }

    @Override
    public synchronized void stop() {
        periodicIo = new PeriodicIO();
    }

    @Override
    public synchronized void writePeriodicOutputs() {
        leftRearWheel.set(ControlMode.PercentOutput, periodicIo.leftRearWheelOutput);
        rightRearWheel.set(ControlMode.PercentOutput, periodicIo.rightRearWheelOutput);
        leftRearJack.set(ControlMode.PercentOutput, periodicIo.leftRearJackOutput);
        rightRearJack.set(ControlMode.PercentOutput, periodicIo.rightRearJackOutput);
        frontJack.set(ControlMode.PercentOutput, periodicIo.frontJackOutput);
    }

    // TODO remove this once we no longer have a desire for manual control
    private synchronized void liftAll() {
        periodicIo.frontJackOutput = DEFAULT_JACK_POWER * FRONT_LIFT_MULTIPLIER;
        periodicIo.leftRearJackOutput = DEFAULT_JACK_POWER * LEFT_REAR_LIFT_MULTIPLIER;
        periodicIo.rightRearJackOutput = DEFAULT_JACK_POWER * RIGHT_REAR_LIFT_MULTIPLIER;
    }

    // TODO remove this once we no longer have a desire for manual control
    private synchronized void retractAll() {
        periodicIo.frontJackOutput = -DEFAULT_JACK_POWER * FRONT_LIFT_MULTIPLIER;
        periodicIo.leftRearJackOutput = -DEFAULT_JACK_POWER * LEFT_REAR_LIFT_MULTIPLIER;
        periodicIo.rightRearJackOutput = -DEFAULT_JACK_POWER * RIGHT_REAR_LIFT_MULTIPLIER;
    }

    // TODO remove this once we no longer have a desire for manual control
    private synchronized void retractFrontJack() {
        periodicIo.frontJackOutput = -DEFAULT_JACK_POWER;
    }

    // TODO remove this once we no longer have a desire for manual control
    private synchronized void jackMod(JackLiftState front, JackLiftState left, JackLiftState right) {
        periodicIo.frontJackOutput = front.getMultiplier() * DEFAULT_JACK_POWER;
        periodicIo.leftRearJackOutput = left.getMultiplier() * DEFAULT_JACK_POWER;
        periodicIo.rightRearJackOutput = right.getMultiplier() * DEFAULT_JACK_POWER;
    }

    private synchronized void runWheels(DriveSignal driveSignal) {
        periodicIo.leftRearWheelOutput = driveSignal.getLeftOutput();
        periodicIo.rightRearWheelOutput = driveSignal.getRightOutput();
    }

    public synchronized void automaticSyncLiftBasic() {
        // TODO confirm that
        //  roll + = robot rear coming up
        //  pitch + = right side coming up
        final double pitchCorrectionKp = 0.05; // %vbus per degree
        final double rollCorrectionKp = 0.05; // %vbus per degree
        final double pitchCorrectionOutput = pitchCorrectionKp * periodicIo.pitch;
        final double rollCorrectionOutput = rollCorrectionKp * periodicIo.roll;
        liftAll();
        // If the roll is positive, the robot is tipping forwards so we should add power to front and subtract from rear
        // If the pitch is positive, the robot is tipping to the left so we should add to the left and subtract from the front
        periodicIo.frontJackOutput += pitchCorrectionOutput;
        periodicIo.leftRearJackOutput += rollCorrectionOutput - pitchCorrectionKp;
        periodicIo.rightRearJackOutput += -rollCorrectionOutput - pitchCorrectionKp;
    }

    public enum JackLiftState {
        LIFT(1.0),
        RETRACT(-1.0),
        NEUTRAL(0.0);

        private double multiplier;

        JackLiftState(double multiplier) {
            this.multiplier = multiplier;
        }

        public double getMultiplier() {
            return multiplier;
        }
    }

    private static class PeriodicIO {
        // Inputs
        double pitch;
        double roll;

        // Outputs
        double leftRearWheelOutput;
        double rightRearWheelOutput;
        double leftRearJackOutput;
        double rightRearJackOutput;
        double frontJackOutput;
    }
}