package frc.robot;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DriverStation;
import frc.subsystem.Subsystem;

import static frc.utils.Constants.ELEVATOR_SHUFFLEBOARD;

public class Elevator2 extends Subsystem {
    private static final double UPPER_POT_VALUE = 1.65;
    private static final double LOWER_POT_VALUE = 3.80;
    private static final double BOTTOM_DIST_FROM_GROUND = (9.0 + (7.0 / 8.0)) * 0.0254;
    private static final double UPPER_DIST_FROM_GROUND = (74.75) * 0.0254;
    private static final double TOTAL_DELTA_HEIGHT = UPPER_DIST_FROM_GROUND - BOTTOM_DIST_FROM_GROUND;
    private static final double TOTAL_DELTA_VOLTAGE = UPPER_POT_VALUE - LOWER_POT_VALUE;
    private static final double TOTAL_DELTA_ENCODER_VALUE = -50.0;
    private static final double HOLD_FEEDFORWARD = -0.06;
    private static final double DESIRED_TOLERANCE = 0.25 * 0.0254;
    private static final double DESIRED_SPEED_AT_TOLERANCE_POINT = 0.03;
    private static final double KP_CUSTOM_PID = DESIRED_SPEED_AT_TOLERANCE_POINT / DESIRED_TOLERANCE;
    private static final double KV_CUSTOM_PID = 0.01;
    private static final double KP_ENCODER_BASED = (DESIRED_SPEED_AT_TOLERANCE_POINT / DESIRED_TOLERANCE) * (TOTAL_DELTA_ENCODER_VALUE / TOTAL_DELTA_HEIGHT);
    private static Elevator2 instance;
    private double voltage = 0.0;
    private double height = 0.0;
    private double encoderPosition = 0.0;
    private double encoderFilteredPosition = 0.0;
    private double temperature = 0.0;
    private CANSparkMax motor;
    private CANEncoder encoder;
    private CANPIDController controller;
    private AnalogInput pot;
    private double power = 0.0;
    private double encoderOffset = 0.0;
    private double lastError = 0.0;

    private Elevator2() {
        pot = new AnalogInput(0);
        motor = new CANSparkMax(13, CANSparkMaxLowLevel.MotorType.kBrushless);
        encoder = new CANEncoder(motor);
        controller = motor.getPIDController();
        controller.setFF(HOLD_FEEDFORWARD);
        controller.setP(KP_ENCODER_BASED);
        controller.setI(0.0);
        controller.setD(0.0);
        controller.setIZone(0.0);
    }

    public static Elevator2 getInstance() {
        if (instance == null) {
            instance = new Elevator2();
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
        voltage = pot.getVoltage();
        height = convertVoltageToHeight(voltage);
        encoderPosition = encoder.getPosition();
        temperature = motor.getMotorTemperature();

        if (voltage > LOWER_POT_VALUE - 0.01) {
            encoderOffset = -encoderPosition;
        }

        if (temperature > 80.0) {
            DriverStation.reportError("ELEVATOR TEMPERATURE TOO HIGH", false);
        }
    }

    @Override
    public void outputTelemetry() {
        encoderFilteredPosition = encoderPosition + encoderOffset;
        ELEVATOR_SHUFFLEBOARD.putNumber("Pot", voltage);
        ELEVATOR_SHUFFLEBOARD.putNumber("Height", height);
        ELEVATOR_SHUFFLEBOARD.putNumber("Height (inches)", height * (1.0 / 0.0254));
        ELEVATOR_SHUFFLEBOARD.putNumber("Encoder (Raw)", encoderPosition);
        ELEVATOR_SHUFFLEBOARD.putNumber("Encoder (Filtered)", encoderFilteredPosition);
        ELEVATOR_SHUFFLEBOARD.putNumber("Encoder at 1.0 meters", convertHeightToEncoder(1.0));
        ELEVATOR_SHUFFLEBOARD.putNumber("Current", motor.getOutputCurrent());
        ELEVATOR_SHUFFLEBOARD.putNumber("Desired Power", power);
        ELEVATOR_SHUFFLEBOARD.putNumber("Temperature", temperature);
    }

    public void manualMovement(double manualPower) {
        power = -manualPower;
        power = (Math.abs(power) < 0.02) ? 0.0 : power;
    }

    public void pidToPosition(ElevatorPosition position) {
        double error = position.getHeight() - height;
        double velocity = lastError - error;
        power = (-error * KP_CUSTOM_PID) + (KV_CUSTOM_PID * velocity) + HOLD_FEEDFORWARD;
        lastError = error;
    }

    @Override
    public void writePeriodicOutputs() {
        if (voltage > LOWER_POT_VALUE) {
            power = power > 0 ? 0 : power;
        } else if (voltage < UPPER_POT_VALUE) {
            power = power < 0 ? HOLD_FEEDFORWARD : power;
        }
        motor.set(power);
    }

    @Override
    public void stop() {

    }

    public enum ElevatorPosition {
        HATCH_LOW(BOTTOM_DIST_FROM_GROUND),
        HATCH_MID(38 * 0.0254),
        HATCH_HIGH(64 * 0.0254),
        CARGO_LOW(21 * 0.0254),
        CARGO_MID(49.25 * 0.0254),
        CARGO_HIGH(74 * 0.0254);

        private double height;

        ElevatorPosition(double height) {
            this.height = height;
        }

        public double getHeight() {
            return height;
        }
    }
}
