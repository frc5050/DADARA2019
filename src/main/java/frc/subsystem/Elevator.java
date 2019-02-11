package frc.subsystem;

import com.revrobotics.*;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

import static frc.utils.Constants.ELEVATOR_NEO;
import static frc.utils.Constants.ELEVATOR_SHUFFLEBOARD;
import static frc.utils.UnitConversions.inchesToMeters;
import static frc.utils.UnitConversions.metersToInches;

public class Elevator extends Subsystem {
    public static final double TOTAL_DELTA_ENCODER_VALUE = -181.22; // revolutions (top - bottom)
    private static final double BOTTOM_DIST_FROM_GROUND = inchesToMeters(9.0 + (4.0 / 8.0));
    private static final double UPPER_DIST_FROM_GROUND = inchesToMeters(74.75);
    public static final double TOTAL_DELTA_HEIGHT = UPPER_DIST_FROM_GROUND - BOTTOM_DIST_FROM_GROUND;
    private static final double REVOLUTIONS_PER_METER = -TOTAL_DELTA_ENCODER_VALUE / TOTAL_DELTA_HEIGHT; // revolutions / meter
    private static final double MANUAL_MOVEMENT_DEADBAND = 0.02;
    private static final double MAX_RPM = 5000;
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
    private double lastErrorMeters = 0.0;
    private double lastTimestamp = Timer.getFPGATimestamp();
    private boolean hasZeroed = false;

    private Elevator() {
        ELEVATOR_SHUFFLEBOARD.putNumber("REVOLUTIONS_PER_METER", REVOLUTIONS_PER_METER);
        bottomLimit = new DigitalInput(2);
        motor = new CANSparkMax(ELEVATOR_NEO, CANSparkMaxLowLevel.MotorType.kBrushless);
        encoder = new CANEncoder(motor);
        controller = motor.getPIDController();
        controller.setFF(0.0);
        controller.setP(3E-5);
        controller.setI(1E-6);
        controller.setD(0);
        controller.setIZone(0);
        controller.setOutputRange(-1.0, 1.0);
    }

    public static Elevator getInstance() {
        if (instance == null) {
            instance = new Elevator();
        }
        return instance;
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
        if (!hasZeroed) {
            if (bottomLimitTriggered) {
                hasZeroed = true;
            }
        }
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
        ELEVATOR_SHUFFLEBOARD.putNumber("Velocity", encoder.getVelocity());
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

    public void manualMovement(double manualPower) {
        usePID = false;
        power = -manualPower;
        power = (Math.abs(power) < MANUAL_MOVEMENT_DEADBAND) ? 0.0 : power;
    }

    public synchronized void pidToPosition(ElevatorPosition position) {
        usePID = true;
        desiredHeight = position.getHeight();
        desiredPosition = position;
        desiredEncoder = convertHeightToEncoder(desiredHeight);
    }

    @Override
    public synchronized void writePeriodicOutputs() {
        double value;
        ControlType controlType;
        if (!usePID) {
            value = power;
            controlType = ControlType.kDutyCycle;
        } else {
            controlType = ControlType.kVelocity;
            double errorMeters = (desiredPosition.getHeight() - height);
            final double kp = -5 * MAX_RPM; // (revolutions / minute) / (meter)
            final double kv = -3300; // (revolutions / minute) / (meter / second)
            // TODO move to readPeriodicInputs
            final double timestamp = Timer.getFPGATimestamp();

            final double velocity = (errorMeters - lastErrorMeters) / (timestamp - lastTimestamp);
            value = (kp * errorMeters) + (kv * velocity);
            value = Math.abs(value) > MAX_RPM ? Math.copySign(MAX_RPM, value) : value;
            lastErrorMeters = errorMeters;
            lastTimestamp = timestamp;
            if (!hasZeroed) {
                controlType = ControlType.kDutyCycle;
                value = 0.2;
            }
        }
        if (bottomLimitTriggered && value > 0) {
            controlType = ControlType.kDutyCycle;
            value = 0;
        }
        controller.setReference(value, controlType);
        previousLimitTriggered = bottomLimitTriggered;
    }

    @Override
    public void stop() {

    }

    public enum ElevatorPosition {
        HATCH_LOW(BOTTOM_DIST_FROM_GROUND),
        HATCH_MID(inchesToMeters(38)),
        HATCH_HIGH(inchesToMeters(64 + 1)),
        CARGO_LOW(inchesToMeters(21)),
        CARGO_MID(inchesToMeters(49.25)),
        CARGO_HIGH(inchesToMeters(73 + 0.25));

        private double height;

        ElevatorPosition(double height) {
            this.height = height;
        }

        public double getHeight() {
            return height;
        }
    }
}
