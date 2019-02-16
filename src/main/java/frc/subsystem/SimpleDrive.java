package frc.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import frc.utils.DriveSignal;

import static frc.utils.Constants.*;

public final class SimpleDrive extends Subsystem {
    private static SimpleDrive instance;
    private final WPI_TalonSRX leftMaster;
    private final VictorSPX leftSlave;
    private final WPI_TalonSRX rightMaster;
    private final VictorSPX rightSlave;
    private final AHRS navX;
    private boolean isBrake = false;
    private PeriodicIO periodicIo;
    private double yawOffset = 0.0;
    private double rollOffset = 0.0;
    private double pitchOffset = 0.0;

    private SimpleDrive() {
        periodicIo = new PeriodicIO();
        leftMaster = new WPI_TalonSRX(LEFT_DRIVE_1);
        leftSlave = new VictorSPX(LEFT_DRIVE_2);
        leftSlave.follow(leftMaster);
        rightMaster = new WPI_TalonSRX(RIGHT_DRIVE_1);
        rightSlave = new VictorSPX(RIGHT_DRIVE_2);
        rightSlave.follow(rightMaster);

        rightMaster.setInverted(true);
        rightSlave.setInverted(true);

        navX = new AHRS(SPI.Port.kMXP);
        setBrakeMode(true);
    }

    // Creates an instance of drive. If there is already an instance, do nothing so it doesn't conflict
    public static SimpleDrive getInstance() {
        if (instance == null) {
            instance = new SimpleDrive();
        }
        return instance;
    }

    // Sets the motors from neutral mode coast to neutral mode brake
    public synchronized void setBrakeMode(boolean brake) {
        if (isBrake != brake) {
            NeutralMode neutralMode = brake ? NeutralMode.Brake : NeutralMode.Coast;
            leftMaster.setNeutralMode(neutralMode);
            leftSlave.setNeutralMode(neutralMode);
            rightMaster.setNeutralMode(neutralMode);
            rightSlave.setNeutralMode(neutralMode);
            isBrake = brake;
        }
    }

    // Changes the mode to joystick input
    public synchronized void setOpenLoop(DriveSignal signal) {
        periodicIo.leftDemand = signal.getLeftOutput();
        periodicIo.rightDemand = signal.getRightOutput();
        periodicIo.leftFeedForward = 0.0;
        periodicIo.rightFeedForward = 0.0;
    }

    // Puts values on the Dashboard (Shuffleboard)
    @Override
    public synchronized void outputTelemetry() {
        DRIVE_SHUFFLEBOARD.putNumber("Left Drive Ticks", periodicIo.leftPositionTicks);
        DRIVE_SHUFFLEBOARD.putNumber("Right Drive Ticks", periodicIo.rightPositionTicks);
        DRIVE_SHUFFLEBOARD.putNumber("Left Drive Ticks", periodicIo.leftPositionTicks);
        DRIVE_SHUFFLEBOARD.putNumber("Right Drive Ticks", periodicIo.rightPositionTicks);
        DRIVE_SHUFFLEBOARD.putNumber("Left Demand", periodicIo.leftDemand);
        DRIVE_SHUFFLEBOARD.putNumber("Right Demand", periodicIo.rightDemand);
        DRIVE_SHUFFLEBOARD.putNumber("Left Feed Forward", periodicIo.leftFeedForward);
        DRIVE_SHUFFLEBOARD.putNumber("Right Feed Forward", periodicIo.rightFeedForward);
        DRIVE_SHUFFLEBOARD.putNumber("Yaw", periodicIo.yaw);
        DRIVE_SHUFFLEBOARD.putNumber("Roll", periodicIo.roll);
        DRIVE_SHUFFLEBOARD.putNumber("Pitch", periodicIo.pitch);
    }

    // Outputs different values based on whether the robot is in teleop or autonomous
    @Override
    public synchronized void writePeriodicOutputs() {
        leftMaster.set(ControlMode.PercentOutput, periodicIo.leftDemand, DemandType.ArbitraryFeedForward, 0.0);
        rightMaster.set(ControlMode.PercentOutput, periodicIo.rightDemand, DemandType.ArbitraryFeedForward, 0.0);
    }

    // Stops the robot
    @Override
    public void stop() {
        setOpenLoop(DriveSignal.NEUTRAL);
    }

    // Resets encoders
    private synchronized void resetEncoders() {
        // TODO should this be a constant
        leftMaster.setSelectedSensorPosition(0, 0, 0);
        rightMaster.setSelectedSensorPosition(0, 0, 0);
        periodicIo = new PeriodicIO();
    }

    @Override
    public void zeroSensors() {
        resetEncoders();
    }

    // Reads the inputs from the sensors, and sets variables
    @Override
    public synchronized void readPeriodicInputs() {
        periodicIo.yaw = navX.getYaw();
        periodicIo.roll = navX.getRoll();
        periodicIo.pitch = navX.getPitch();
    }

    public synchronized void resetNavX() {
        yawOffset = -periodicIo.yaw;
        rollOffset = -periodicIo.roll;
        pitchOffset = -periodicIo.pitch;
    }

    public synchronized double getYaw() {
        return periodicIo.yaw + yawOffset;
    }

    public synchronized double getPitch() {
        return periodicIo.pitch + pitchOffset;
    }

    public synchronized double getRoll() {
        return periodicIo.roll + rollOffset;
    }

    public synchronized double getYawRaw() {
        return periodicIo.yaw;
    }

    public synchronized double getPitchRaw() {
        return periodicIo.pitch;
    }

    public synchronized double getRollRaw() {
        return periodicIo.roll;
    }

    // All the periodic values
    private static class PeriodicIO {
        // Input
        int leftPositionTicks;
        int rightPositionTicks;
        double yaw;
        double roll;
        double pitch;

        // Output
        double leftDemand;
        double rightDemand;
        double leftFeedForward;
        double rightFeedForward;
    }
}
