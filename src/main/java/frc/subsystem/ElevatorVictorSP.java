package frc.subsystem;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.VictorSP;
import frc.utils.DriveSignal;
import frc.utils.UnitConversions;

import static frc.utils.Constants.*;

public class ElevatorVictorSP extends Subsystem {
    private static final double COUNTS_PER_REVOLUTION = 1024;
    private static final double DISTANCE_PER_REVOLUTION = UnitConversions.inchesToMeters(1.732);
    private static final double DISTANCE_PER_COUNT = DISTANCE_PER_REVOLUTION * (1.0 / COUNTS_PER_REVOLUTION);
    private static ElevatorVictorSP instance;
    // TODO do we need to add right side to anything else after slaving?
    private final VictorSP left;
    private final VictorSP right;
    private final Encoder leftEncoder;
    private final Encoder rightEncoder;
    // TODO add limit switch validation against cargo/hatches hitting it
    private final DigitalInput bottomLimit;
    private PeriodicIO periodicIo = new PeriodicIO();

    private ElevatorVictorSP() {
        left = new VictorSP(LEFT_LIFT_NEO);
        right = new VictorSP(RIGHT_LIFT_NEO);
        leftEncoder = new Encoder(0, 1);
        rightEncoder = new Encoder(2, 3);

        // TODO do we need to swap top or bottom?
        bottomLimit = new DigitalInput(0);

        // TODO check inversion and make sure that following works
        right.setInverted(true);
    }

    public static ElevatorVictorSP getInstance() {
        if (instance == null) {
            instance = new ElevatorVictorSP();
        }
        return instance;
    }

    @Override
    public synchronized void readPeriodicInputs() {
        periodicIo.leftRawPosition = leftEncoder.getRaw() / DISTANCE_PER_COUNT;
        periodicIo.leftDistance = periodicIo.leftRawPosition / DISTANCE_PER_COUNT;
        periodicIo.leftVelocity = leftEncoder.getRate() / DISTANCE_PER_COUNT;
        periodicIo.rightRawPosition = rightEncoder.getRate() / DISTANCE_PER_COUNT;
        periodicIo.rightDistance = periodicIo.rightRawPosition / DISTANCE_PER_COUNT;
        periodicIo.rightVelocity = rightEncoder.getRate() / DISTANCE_PER_COUNT;
        periodicIo.bottomLimitHit = bottomLimit.get();
        // TODO reimplement this with a branch based on whether we have cargo or not
        //  periodicIo.feedForward = FEEDFORWARD_NO_CARGO;
    }

    @Override
    public synchronized void writePeriodicOutputs() {
        // left.set(leftStateMachine.update(periodicIo.leftRawPosition, ))
    }

    @Override
    public void outputTelemetry() {
        // TODO(Raina)
        ELEVATOR_SHUFFLEBOARD.putNumber("Left Raw Position", periodicIo.leftRawPosition);
        ELEVATOR_SHUFFLEBOARD.putNumber("Right Raw Position", periodicIo.rightRawPosition);
        ELEVATOR_SHUFFLEBOARD.putNumber("Left Distance", periodicIo.leftDistance);
        ELEVATOR_SHUFFLEBOARD.putNumber("Right Distance", periodicIo.rightDistance);
        ELEVATOR_SHUFFLEBOARD.putNumber("Right Velocity", periodicIo.rightVelocity);
        ELEVATOR_SHUFFLEBOARD.putNumber("Left Velocity", periodicIo.rightVelocity);
        ELEVATOR_SHUFFLEBOARD.putBoolean("Bottom Limit Hit", bottomLimit.get());
    }

    public void manual(DriveSignal driveSignal) {
        left.set(driveSignal.getLeftOutput());
        right.set(driveSignal.getLeftOutput());
    }

    @Override
    public void stop() {
        left.stopMotor();
        right.stopMotor();
    }

    private static class PeriodicIO {
        // Inputs
        double leftRawPosition;
        double leftVelocity;
        double rightRawPosition;
        double rightDistance;
        double leftDistance;
        double rightVelocity;
        boolean bottomLimitHit;

        // TODO implement me with query about having a ball
        boolean cargoHeld;
    }
}
