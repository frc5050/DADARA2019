package frc.subsystem;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SPI;
import frc.loops.Loop;
import frc.loops.LooperInterface;
import frc.utils.DriveSignal;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.modifiers.TankModifier;

import static frc.utils.Constants.*;
import static frc.utils.UnitConversions.inchesToMeters;

// TODO verify everythign
// All setup variables for this subsystem
public class Drive extends Subsystem {
    private static final double DRIVE_WHEEL_DIAMETER = inchesToMeters(6.0);
    private static final int DRIVE_TICKS_PER_ROTATION = 4096;
    private static final double DRIVE_TICKS_PER_ROTATION_DOUBLE = (double) DRIVE_TICKS_PER_ROTATION;
    // TODO remeasure on a bot
    private static final double DRIVEBASE_WIDTH = 0.56515;
    private static Drive instance;
    private boolean isBrake = false;
    private DriveState state = DriveState.OPEN_LOOP;
    private WPI_TalonSRX leftMaster;
    private VictorSPX leftSlave;
    private WPI_TalonSRX rightMaster;
    private VictorSPX rightSlave;
    private PeriodicIO periodicIo;
    private AHRS navX;
    private double gyroOffset = 0.0;
    private Trajectory trajectory = null;
    private Trajectory leftTrajectory = null;
    private Trajectory rightTrajectory = null;
    private int lastTrajectoryValue = 0;
    private int trajectoryValues = 0;
    private final Loop loop = new Loop() {
        @Override
        public void onStart(double timestamp) {
            synchronized (Drive.this) {
                setOpenLoop(DriveSignal.BRAKE);
                setBrakeMode(false);
            }
        }

        @Override
        public void onLoop(double timestamp) {
            synchronized (Drive.this) {
                switch (state) {
                    case OPEN_LOOP:
                        break;
                    case PATH_FOLLOWING:
                        updatePathFollower();
                        break;
                    default:
                        DriverStation.reportError("Drive state set to: " + state + " unexpectedly", false);
                }
            }
        }

        @Override
        public void onStop(double timestamp) {

        }
    };

    private Drive() {
        periodicIo = new PeriodicIO();

        leftMaster = new WPI_TalonSRX(LEFT_DRIVE_1);
        configureMaster(leftMaster, true);
        leftSlave = new VictorSPX(LEFT_DRIVE_2);
        leftSlave.follow(leftMaster);

        rightMaster = new WPI_TalonSRX(RIGHT_DRIVE_1);
        configureMaster(rightMaster, false);
        rightSlave = new VictorSPX(RIGHT_DRIVE_2);
        rightSlave.follow(rightMaster);

        rightMaster.setInverted(true);
        rightSlave.setInverted(true);

        navX = new AHRS(SPI.Port.kMXP);

//        initGainsOnShuffleBoard();
    }

    // Creates an instance of drive. If there is already an instance, do nothing so it doesn't conflict
    public static Drive getInstance() {
        if (instance == null) {
            instance = new Drive();
        }
        return instance;
    }

    // TODO change this if the gyro changes
    private static double handleGyroInput(double heading, double gyroOffset) {
        // 0 to 360: gyroHeading = (gyroHeading + gyroOffset) % 360.0;
        return (((heading + gyroOffset) - 180.0) % 360.0) - 180.0;
    }

    // Determines the velocity of the robot relative to the amount of encoder ticks
    private static double wheelVelocityTicksPer100msToVelocity(double velocityTicksPer100ms) {
        return velocityTicksPer100ms * 10.0 / DRIVE_TICKS_PER_ROTATION_DOUBLE * Math.PI * DRIVE_WHEEL_DIAMETER;
    }

    // Configures PID values for the motors
    private static void reloadGains(WPI_TalonSRX talon, double kP, double kI, double kD, double kF, int kIz, int timeout) {
        talon.config_kP(0, kP, timeout);
        talon.config_kI(0, kI, timeout);
        talon.config_kD(0, kD, timeout);
        talon.config_kF(0, kF, timeout);
        talon.config_IntegralZone(0, kIz, timeout);

    }

    private void configureMaster(WPI_TalonSRX talon, boolean left) {
        // TODO should the period be in a constant?
        talon.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 5, 100);

        // Primary closed-loop, 100 ms timeout
        // TODO should the period be in a constant?
        final ErrorCode sensorPresent = talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 100);

        if (sensorPresent != ErrorCode.OK) {
            DriverStation.reportError("Could not detect " + (left ? "left" : "right") + " drive encoder: " + sensorPresent, false);
        }
        // Configures the Talons with inversions and sensor values
        talon.setInverted(!left);
        // TODO check sensor phase
        talon.setSensorPhase(true);
        talon.enableVoltageCompensation(true);
        talon.configVoltageCompSaturation(12.0, CAN_TIMEOUT_MS);
        // TODO tune this period
        talon.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_50Ms, CAN_TIMEOUT_MS);
        talon.configVelocityMeasurementWindow(1, CAN_TIMEOUT_MS);
        talon.configClosedloopRamp(DRIVE_VOLTAGE_RAMP_RATE, CAN_TIMEOUT_MS);
        // TODO should this be in a constant?
        talon.configNeutralDeadband(0.04, 0);
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
        if (state != DriveState.OPEN_LOOP) {
            setBrakeMode(false);
            // TODO should this be in a constant?
            leftMaster.configNeutralDeadband(0.04, 0);
            rightMaster.configNeutralDeadband(0.04, 0);
            state = DriveState.OPEN_LOOP;
        }
        periodicIo.leftDemand = signal.getLeftOutput();
        periodicIo.rightDemand = signal.getRightOutput();
        periodicIo.leftFeedForward = 0.0;
        periodicIo.rightFeedForward = 0.0;
    }

    // Puts values on the Dashboard (Shuffleboard)
    @Override
    public void outputTelemetry() {
//        DRIVE_SHUFFLEBOARD.putNumber("Left Drive Distance (m)", periodicIo.leftDistance);
//        DRIVE_SHUFFLEBOARD.putNumber("Right Drive Distance (m)", periodicIo.rightDistance);
//        DRIVE_SHUFFLEBOARD.putNumber("Left Drive Ticks", periodicIo.leftPositionTicks);
//        DRIVE_SHUFFLEBOARD.putNumber("Right Drive Ticks", periodicIo.rightPositionTicks);
        DRIVE_SHUFFLEBOARD.putNumber("Left Demand", periodicIo.leftDemand);
        DRIVE_SHUFFLEBOARD.putNumber("Right Demand", periodicIo.rightDemand);
        DRIVE_SHUFFLEBOARD.putNumber("Left Feed Forward", periodicIo.leftFeedForward);
        DRIVE_SHUFFLEBOARD.putNumber("Right Feed Forward", periodicIo.rightFeedForward);
//        DRIVE_SHUFFLEBOARD.putNumber("Left Velocity Ticks Per 100 ms", periodicIo.leftVelocityTicksPer100ms);
//        DRIVE_SHUFFLEBOARD.putNumber("Right Velocity Ticks Per 100 ms", periodicIo.rightVelocityTicksPer100ms);
//        DRIVE_SHUFFLEBOARD.putNumber("Right Velocity", periodicIo.rightVelocity);
//        DRIVE_SHUFFLEBOARD.putNumber("Left Velocity", periodicIo.leftVelocity);
//        DRIVE_SHUFFLEBOARD.putNumber("Gyro Heading", periodicIo.gyroHeading);
        DRIVE_SHUFFLEBOARD.putNumber("Yaw", periodicIo.yaw);
        DRIVE_SHUFFLEBOARD.putNumber("Roll", periodicIo.roll);
        DRIVE_SHUFFLEBOARD.putNumber("Pitch", periodicIo.pitch);
    }

    // Outputs different values based on whether the robot is in teleop or autonomous
    @Override
    public synchronized void writePeriodicOutputs() {
        if (state == DriveState.OPEN_LOOP) {
            leftMaster.set(ControlMode.PercentOutput, periodicIo.leftDemand, DemandType.ArbitraryFeedForward, 0.0);
            rightMaster.set(ControlMode.PercentOutput, periodicIo.rightDemand, DemandType.ArbitraryFeedForward, 0.0);
        } else if (state == DriveState.PATH_FOLLOWING) {
            leftMaster.set(ControlMode.Velocity, periodicIo.leftDemand, DemandType.ArbitraryFeedForward, periodicIo.leftFeedForward);
            rightMaster.set(ControlMode.Velocity, periodicIo.rightDemand, DemandType.ArbitraryFeedForward, periodicIo.rightFeedForward);
        }
    }

    // Stops the robot
    @Override
    public void stop() {
        setOpenLoop(DriveSignal.NEUTRAL);
    }

    // Creates a trajectory and sets it to the motors, changing the case to Path following
    public synchronized void setTrajectory(Trajectory trajectory) {
        if (trajectory != null) {
            this.trajectory = trajectory;
            TankModifier modifier = new TankModifier(trajectory).modify(DRIVEBASE_WIDTH);
            leftTrajectory = modifier.getLeftTrajectory();
            rightTrajectory = modifier.getRightTrajectory();
            lastTrajectoryValue = 0;
            trajectoryValues = trajectory.length();
            state = DriveState.PATH_FOLLOWING;
        }
    }

    // Checks for if the trajectory is completed
    public synchronized boolean isDone() {
        if (trajectory == null || state != DriveState.PATH_FOLLOWING) {
            return false;
        }
        return trajectoryValues > lastTrajectoryValue;
    }

    // Checks for path completion and resets the Path followers for the desired velocity if not.
    public synchronized void updatePathFollower() {
        if (state == DriveState.PATH_FOLLOWING) {
            lastTrajectoryValue++;
            if (!this.isDone()) {
                double leftVelocity = leftTrajectory.get(lastTrajectoryValue).velocity;
                double rightVelocity = rightTrajectory.get(lastTrajectoryValue).velocity;
                setVelocity(new DriveSignal(leftVelocity, rightVelocity), DriveSignal.BRAKE);
            } else {
                setVelocity(DriveSignal.BRAKE, DriveSignal.BRAKE);
            }
        } else {
            DriverStation.reportError("Drive is not in path follower mode", false);
        }
    }

    // Resets encoders
    public synchronized void resetEncoders() {
        // TODO should this be a constant
        leftMaster.setSelectedSensorPosition(0, 0, 0);
        rightMaster.setSelectedSensorPosition(0, 0, 0);
        periodicIo = new PeriodicIO();
    }

    // Configures the gyro heading
    public synchronized void setHeading(double heading) {
        gyroOffset = heading - navX.getYaw();
    }

    @Override
    public void zeroSensors() {
        setHeading(0.0);
        resetEncoders();
    }

    @Override
    public void registerEnabledLoops(LooperInterface looper) {
        looper.registerLoop(loop);
    }

    // Reads the inputs from the sensors, and sets variables
    @Override
    public void readPeriodicInputs() {
//        double prevLeftTicks = periodicIo.leftPositionTicks;
//        double prevRightTicks = periodicIo.rightPositionTicks;
//        periodicIo.leftPositionTicks = leftMaster.getSelectedSensorPosition(0);
//        periodicIo.rightPositionTicks = rightMaster.getSelectedSensorPosition(0);
//        periodicIo.leftVelocityTicksPer100ms = leftMaster.getSelectedSensorVelocity(0);
//        periodicIo.rightVelocityTicksPer100ms = rightMaster.getSelectedSensorVelocity(0);
//        periodicIo.leftVelocity = wheelVelocityTicksPer100msToVelocity(periodicIo.leftVelocityTicksPer100ms);
//        periodicIo.rightVelocity = wheelVelocityTicksPer100msToVelocity(periodicIo.rightVelocityTicksPer100ms);
        periodicIo.yaw = navX.getYaw();
        periodicIo.roll = navX.getRoll();
        periodicIo.pitch = navX.getPitch();
//        periodicIo.gyroHeading = handleGyroInput(periodicIo.yaw, gyroOffset);

//        double deltaLeftTicks = ((periodicIo.leftPositionTicks - prevLeftTicks) / DRIVE_TICKS_PER_ROTATION_DOUBLE) * Math.PI;
//        periodicIo.leftDistance += deltaLeftTicks * DRIVE_WHEEL_DIAMETER;

//        double deltaRightTicks = ((periodicIo.rightPositionTicks - prevRightTicks) / DRIVE_TICKS_PER_ROTATION_DOUBLE) * Math.PI;
//        periodicIo.rightDistance += deltaRightTicks * DRIVE_WHEEL_DIAMETER;


//        // TODO remove this once we tune the gains properly
//        double kP = DRIVE_SHUFFLEBOARD.getNumber("kP", 1.0);
//        double kI = DRIVE_SHUFFLEBOARD.getNumber("kI", 0.0);
//        double kD = DRIVE_SHUFFLEBOARD.getNumber("kD", 0.0);
//        double kF = DRIVE_SHUFFLEBOARD.getNumber("kF", 0.0);
//        double kIz = DRIVE_SHUFFLEBOARD.getNumber("kIz", 0.0);
//
//        boolean needToReloadGains =
//                kP != periodicIo.kP ||
//                        kI != periodicIo.kI ||
//                        kD != periodicIo.kD ||
//                        kF != periodicIo.kF ||
//                        kIz != periodicIo.kIz;
//        if (needToReloadGains) {
//            reloadGains();
//        }
    }
//
//    // TODO remove this once we tune the gains properly
//    private synchronized void reloadGains() {
//        final int longTimeout = 100; // ms
//        reloadGains(leftMaster, periodicIo.kP, periodicIo.kI, periodicIo.kD, periodicIo.kF, (int) periodicIo.kIz, longTimeout);
//        reloadGains(rightMaster, periodicIo.kP, periodicIo.kI, periodicIo.kD, periodicIo.kF, (int) periodicIo.kIz, longTimeout);
//    }

    //    // TODO remove this once we tune the gains properly
//    private void initGainsOnShuffleBoard() {
//        DRIVE_SHUFFLEBOARD.putNumber("kP", 1.0);
//        DRIVE_SHUFFLEBOARD.putNumber("kI", 0.0);
//        DRIVE_SHUFFLEBOARD.putNumber("kD", 0.0);
//        DRIVE_SHUFFLEBOARD.putNumber("kF", 0.0);
//        DRIVE_SHUFFLEBOARD.putNumber("kIz", 0.0);
//    }
    // Sets the velocity the motors should follow while path following
    public synchronized void setVelocity(DriveSignal velocities, DriveSignal feedForwards) {
        if (state != DriveState.PATH_FOLLOWING) {
            setBrakeMode(true);
            // TODO should this be a constant?
            leftMaster.selectProfileSlot(0, 0);
            rightMaster.selectProfileSlot(0, 0);
            leftMaster.configNeutralDeadband(0.0, 0);
            rightMaster.configNeutralDeadband(0.0, 0);
            state = DriveState.PATH_FOLLOWING;
        }
        periodicIo.leftDemand = velocities.getLeftOutput();
        periodicIo.rightDemand = velocities.getRightOutput();
        periodicIo.leftFeedForward = velocities.getLeftOutput();
        periodicIo.rightFeedForward = velocities.getRightOutput();
    }

    public synchronized double getYaw() {
        return periodicIo.yaw;
    }

    public synchronized double getPitch() {
        return periodicIo.pitch;
    }

    public synchronized double getRoll() {
        return periodicIo.roll;
    }

    // List of possible states/modes
    private enum DriveState {
        OPEN_LOOP,
        PATH_FOLLOWING
    }

    // All the periodic values
    private static class PeriodicIO {
        // Input
//        double leftPositionTicks;
//        double rightPositionTicks;
//        double leftVelocityTicksPer100ms;
//        double rightVelocityTicksPer100ms;
//        double leftVelocity;
//        double rightVelocity;
//        double gyroHeading;
//        double leftDistance;
//        double rightDistance;
        double yaw;
        double roll;
        double pitch;

//        double kP;
//        double kI;
//        double kD;
//        double kF;
//        double kIz;

        // Output
        double leftDemand;
        double rightDemand;
        double leftFeedForward;
        double rightFeedForward;
    }
}
