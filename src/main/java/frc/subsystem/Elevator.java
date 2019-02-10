package frc.subsystem;

import com.revrobotics.*;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import frc.utils.CheapCanPidController;

import javax.swing.text.Highlighter;

import static frc.utils.Constants.*;
import static frc.utils.UnitConversions.inchesToMeters;
import static frc.utils.UnitConversions.metersToInches;

public class Elevator extends Subsystem {
    private static final double UPPER_POT_VALUE = 1.65;
    private static final double LOWER_POT_VALUE = 3.80;
    private static final double BOTTOM_DIST_FROM_GROUND = inchesToMeters(9.0 + (4.0 / 8.0));
    private static final double UPPER_DIST_FROM_GROUND = inchesToMeters(74.75);
    private static final double TOTAL_DELTA_HEIGHT = UPPER_DIST_FROM_GROUND - BOTTOM_DIST_FROM_GROUND;
    private static final double TOTAL_DELTA_VOLTAGE = UPPER_POT_VALUE - LOWER_POT_VALUE;
    private static final double TOTAL_DELTA_ENCODER_VALUE = -181.22; // revolutions (top - bottom)
    private static final double HOLD_FEEDFORWARD = -0.02;
    private static final double DESIRED_TOLERANCE_METERS = inchesToMeters(0.125);
    private static final double DESIRED_SPEED_AT_TOLERANCE_POINT = 0.3;
    private static final double KP_CUSTOM_PID = DESIRED_SPEED_AT_TOLERANCE_POINT / DESIRED_TOLERANCE_METERS;
    private static final double KV_CUSTOM_PID = 0.01;

    private static final double REVOLUTIONS_PER_METER = -TOTAL_DELTA_ENCODER_VALUE / TOTAL_DELTA_HEIGHT; // revolutions / meter
    private static final double REVOLUTION_TOLERANCE = REVOLUTIONS_PER_METER * DESIRED_TOLERANCE_METERS; // revolutions
    private static final double KP_ENCODER_BASED_LOW = 2.0;
    private static final double KP_ENCODER_BASED_MID = 2.0;
    private static final double KP_ENCODER_BASED_HIGH = 2.0;
    private static Elevator instance;
    private final DigitalInput bottomLimit;
    private final CANSparkMax motor;
    private final CANEncoder encoder;
    private final CANPIDController controller;
    private double height = 0.0;
    private double encoderPosition = 0.0;
    private double encoderFilteredPosition = 0.0;
    private double temperature = 0.0;
    private double power = 0.0;
    private double encoderOffset = 0.0;
    private boolean bottomLimitTriggered = false;
    private boolean usePID = false;
    private boolean previousLimitTriggered = false;
    private double desiredHeight = 0.0;
    private double desiredEncoder = 0.0;
    private ElevatorPosition desiredPosition = ElevatorPosition.HATCH_LOW;

    private Elevator() {
        ELEVATOR_SHUFFLEBOARD.putNumber("REVOLUTIONS_PER_METER", REVOLUTIONS_PER_METER);
        bottomLimit = new DigitalInput(2);
        motor = new CANSparkMax(ELEVATOR_NEO, CANSparkMaxLowLevel.MotorType.kBrushless);
        encoder = new CANEncoder(motor);
        controller = motor.getPIDController();
        controller.setFF(HOLD_FEEDFORWARD);
        controller.setP(0.0);
        controller.setI(0.0);
        controller.setD(0.01);
        controller.setIZone(0.1);
    }

    public static Elevator getInstance() {
        if (instance == null) {
            instance = new Elevator();
        }
        return instance;
    }

    private double convertVoltageToHeight(double voltage) {
        return BOTTOM_DIST_FROM_GROUND + ((voltage - LOWER_POT_VALUE) / TOTAL_DELTA_VOLTAGE) * TOTAL_DELTA_HEIGHT;
    }

    private double convertEncoderToHeight(double encoderValue) {
        return BOTTOM_DIST_FROM_GROUND + ((encoderValue + encoderOffset) / TOTAL_DELTA_ENCODER_VALUE) * TOTAL_DELTA_HEIGHT;
    }

    private double convertHeightToEncoder(double height) {
        return ((height - BOTTOM_DIST_FROM_GROUND) / TOTAL_DELTA_HEIGHT) * TOTAL_DELTA_ENCODER_VALUE - encoderOffset;
    }

    @Override
    public void readPeriodicInputs() {
        bottomLimitTriggered = bottomLimit.get();
        encoderPosition = encoder.getPosition();
        temperature = motor.getMotorTemperature();
        if (bottomLimitTriggered) {
            encoderOffset = -encoderPosition;
        }
        height = convertEncoderToHeight(encoderPosition);

        if (temperature > 80.0) {
            DriverStation.reportError("ELEVATOR TEMPERATURE TOO HIGH", false);
        }
    }

    @Override
    public void outputTelemetry() {
        encoderFilteredPosition = encoderPosition + encoderOffset;
        ELEVATOR_SHUFFLEBOARD.putNumber("Height", height);
        double estimatedEncoder = convertHeightToEncoder(height);
        double estimatedEncoderError = encoderPosition - estimatedEncoder;
        double closedLoopPositionError = desiredEncoder - encoderPosition;
        double error = desiredPosition.getHeight() - height;
        ELEVATOR_SHUFFLEBOARD.putNumber("Height To Encoder", convertHeightToEncoder(height));
        ELEVATOR_SHUFFLEBOARD.putNumber("Height To Encoder Error (revs)", estimatedEncoderError);
        ELEVATOR_SHUFFLEBOARD.putNumber("Height (inches)", metersToInches(height));
        ELEVATOR_SHUFFLEBOARD.putNumber("Encoder (Raw)", encoderPosition);
        ELEVATOR_SHUFFLEBOARD.putNumber("Encoder (Filtered)", encoderFilteredPosition);
        ELEVATOR_SHUFFLEBOARD.putNumber("Current", motor.getOutputCurrent());
        ELEVATOR_SHUFFLEBOARD.putNumber("Desired Power", power);
        ELEVATOR_SHUFFLEBOARD.putNumber("Desired Height", desiredHeight);
        ELEVATOR_SHUFFLEBOARD.putNumber("Desired Encoder", desiredEncoder);
        ELEVATOR_SHUFFLEBOARD.putNumber("Closed Loop Position Error", closedLoopPositionError);
        ELEVATOR_SHUFFLEBOARD.putNumber("Temperature", temperature);
        ELEVATOR_SHUFFLEBOARD.putBoolean("Bottom Limit Triggered", bottomLimitTriggered);
        ELEVATOR_SHUFFLEBOARD.putBoolean("Use PID", usePID);
        ELEVATOR_SHUFFLEBOARD.putNumber("Error (m)", error);
    }

    private static final double MANUAL_MOVEMENT_DEADBAND = 0.02;

    public void manualMovement(double manualPower) {
        usePID = false;
        power = -manualPower;
        power = (Math.abs(power) < MANUAL_MOVEMENT_DEADBAND) ? 0.0 : power;
    }

    public void pidToPosition(ElevatorPosition position) {
        usePID = true;
        if (desiredPosition != position) {
            switch (position) {
                case HATCH_LOW:
                    controller.setP(KP_ENCODER_BASED_LOW);
                    break;
                case HATCH_MID:
                    controller.setP(KP_ENCODER_BASED_MID);
                    break;
                case HATCH_HIGH:
                    controller.setP(KP_ENCODER_BASED_HIGH);
                    break;
                case CARGO_LOW:
                    controller.setP(KP_ENCODER_BASED_LOW);
                    break;
                case CARGO_MID:
                    controller.setP(KP_ENCODER_BASED_MID);
                    break;
                case CARGO_HIGH:
                    controller.setP(KP_ENCODER_BASED_HIGH);
                    break;
            }
            desiredPosition = position;
        }
        desiredHeight = position.getHeight();
        desiredEncoder = convertHeightToEncoder(desiredHeight);
//        double error = position.getHeight() - height;
//        double velocity = lastError - error;
//        power = (-error * KP_CUSTOM_PID) + (KV_CUSTOM_PID * velocity) + HOLD_FEEDFORWARD;
//        lastError = error;
    }

    @Override
    public void writePeriodicOutputs() {
        if(desiredPosition == ElevatorPosition.CARGO_HIGH || desiredPosition == ElevatorPosition.HATCH_HIGH){
            if(Math.abs(desiredPosition.getHeight() - height) < 0.5 * 0.01){
                System.out.println("changed p to small err");
                controller.setP(1.0);
            } else {
                System.out.println("changed p to lrg err");
                controller.setP(KP_ENCODER_BASED_HIGH);
            }
        }
        if (!usePID) {
            if (bottomLimitTriggered) {
                power = power > 0 ? 0 : power;
            }
            motor.set(power);
        } else {
            if (bottomLimitTriggered != previousLimitTriggered) {
                if (bottomLimitTriggered) {
                    controller.setOutputRange(-1.0, 0.0);
                } else {
                    controller.setOutputRange(-1.0, 1.0);
                }
            }
            controller.setReference(desiredEncoder, ControlType.kPosition);
        }
        previousLimitTriggered = bottomLimitTriggered;
    }

    @Override
    public void stop() {

    }

    public enum ElevatorPosition {
        HATCH_LOW(BOTTOM_DIST_FROM_GROUND + inchesToMeters(4)),
        HATCH_MID(inchesToMeters(38)),
        HATCH_HIGH(inchesToMeters(64+1)),
        CARGO_LOW(inchesToMeters(21)),
        CARGO_MID(inchesToMeters(49.25)),
        CARGO_HIGH(inchesToMeters(74+1));

        private double height;

        ElevatorPosition(double height) {
            this.height = height;
        }

        public double getHeight() {
            return height;
        }
    }
}
