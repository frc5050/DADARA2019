package frc.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DigitalInput;
import frc.loops.Looper;
import frc.utils.Constants;

import static frc.utils.Constants.HATCH_SHUFFLEBOARD;


// TODO pretty much this whole thing
//  invert whatever needs to be inverted
//  setup sensors &|| closed loop control for this
public class HatchMechanism extends Subsystem {
    private static final double HATCH_SPEED_MULTIPLIER = 0.5;
    private static final double ENCODER_COUNTS_PER_REVOLUTION = 1024.0;
    private static final double REDUCTION_BETWEEN_ENCODER_AND_OUTPUT = 10.0;
    private static final double ROTATIONS_TO_ENCODER = (ENCODER_COUNTS_PER_REVOLUTION * REDUCTION_BETWEEN_ENCODER_AND_OUTPUT);
    private static final double ENCODER_TO_ROTATIONS = 1.0 / ROTATIONS_TO_ENCODER;
    private static final double MAXIMUM_FORWARD_PERCENT = 1.0;
    private static final double MAXIMUM_REVERSE_PERCENT = -1.0;
    private static final int SETTINGS_TIMEOUT = 30; // ms
    private static final int PERIOD_MS = (int) (Looper.PERIOD * 1000);
    private static HatchMechanism instance;
    private final WPI_TalonSRX hatch;
    private final DigitalInput upperLimitSwitch;
    private double demand = 0;
    private ControlMode controlType = ControlMode.PercentOutput;
    private boolean limitHit = false;
    private double encoderValue;
    private boolean zeroed = false;

    private HatchMechanism() {
        upperLimitSwitch = new DigitalInput(1);
        hatch = new WPI_TalonSRX(Constants.HATCH);
        // Set the encoder's direction
        hatch.configFactoryDefault();

        hatch.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, SETTINGS_TIMEOUT);
        hatch.setInverted(false);
        hatch.setSensorPhase(false);

        hatch.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, PERIOD_MS, SETTINGS_TIMEOUT);
        hatch.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, PERIOD_MS, SETTINGS_TIMEOUT);

        hatch.configNominalOutputReverse(0.0, SETTINGS_TIMEOUT);
        hatch.configNominalOutputForward(0.0, SETTINGS_TIMEOUT);
        hatch.configPeakOutputForward(MAXIMUM_FORWARD_PERCENT);
        hatch.configPeakOutputReverse(MAXIMUM_REVERSE_PERCENT);

        hatch.config_kF(0, 0.0); //todo
        hatch.config_kP(0, 2.0); //todo
        hatch.config_kI(0, 0.0); //todo
        hatch.config_kD(0, 0.0); //todo

        hatch.configMotionCruiseVelocity(2400, SETTINGS_TIMEOUT);
        hatch.configMotionAcceleration(900, SETTINGS_TIMEOUT);

        hatch.setSelectedSensorPosition(0, 0, SETTINGS_TIMEOUT);

        zeroed = false;
    }

    public static HatchMechanism getInstance() {
        if (instance == null) {
            instance = new HatchMechanism();
        }
        return instance;
    }

    @Override
    public synchronized void readPeriodicInputs() {
        boolean upperLimitTouched = upperLimitSwitch.get();
        if(upperLimitTouched){
            zeroed = true;
            if(!limitHit){
                hatch.setSelectedSensorPosition(0, 0, SETTINGS_TIMEOUT);
            }
            hatch.configPeakOutputForward(0, SETTINGS_TIMEOUT);
            hatch.configPeakOutputReverse(MAXIMUM_REVERSE_PERCENT, SETTINGS_TIMEOUT);
        } else {
            hatch.configPeakOutputForward(MAXIMUM_FORWARD_PERCENT, SETTINGS_TIMEOUT);
            hatch.configPeakOutputReverse(MAXIMUM_REVERSE_PERCENT, SETTINGS_TIMEOUT);
        }
        limitHit = upperLimitTouched;
        encoderValue = hatch.getSelectedSensorPosition(0);
    }

    @Override
    public synchronized void writePeriodicOutputs() {
        hatch.set(controlType, demand);
    }

    public synchronized void setOpenLoop(double power) {
        controlType = ControlMode.PercentOutput;
        demand = power * MAXIMUM_FORWARD_PERCENT;
    }

    public synchronized void setPosition(double rotations) {

        controlType = ControlMode.MotionMagic;
        if(zeroed) {
            demand = 0;
        } else {
            final double maxPosition = -4198;
            final double minPosition = 0;
            final double range = maxPosition - minPosition;
            final double modifiedJoystick = rotations + 1;
            demand = (modifiedJoystick / 2.0) * maxPosition;
//            demand = encoderValue + 2000;
        }
    }

    private double convertRotationsToEncoder(double percentage) {
        return percentage * ROTATIONS_TO_ENCODER;
    }

    @Override
    public void outputTelemetry() {
        double percentRotation = encoderValue * ENCODER_TO_ROTATIONS;
        HATCH_SHUFFLEBOARD.putNumber("Hatch sensor encoder", encoderValue);
        HATCH_SHUFFLEBOARD.putNumber("Demand", demand);
        HATCH_SHUFFLEBOARD.putNumber("Hatch sensor percent rotation", percentRotation);
        HATCH_SHUFFLEBOARD.putNumber("Hatch sensor angle downwards", percentRotation * 360);
        HATCH_SHUFFLEBOARD.putNumber("Percent Output", hatch.getMotorOutputPercent());
        HATCH_SHUFFLEBOARD.putNumber("Velocity", hatch.getSelectedSensorVelocity(0));
        HATCH_SHUFFLEBOARD.putNumber("Error", hatch.getClosedLoopError(0));
        HATCH_SHUFFLEBOARD.putBoolean("Clicked", limitHit);
    }

    @Override
    public void stop() {
        hatch.stopMotor();
    }
}
