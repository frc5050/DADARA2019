package frc.subsystem;

import com.revrobotics.*;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import frc.utils.PidfConstants;

import static frc.utils.Constants.ELEVATOR_NEO;
import static frc.utils.Constants.ELEVATOR_SHUFFLEBOARD;
import static frc.utils.UnitConversions.inchesToMeters;
import static frc.utils.UnitConversions.metersToInches;

public class Elevator extends Subsystem {
    private static final double TOTAL_DELTA_ENCODER_VALUE = -181.22; // revolutions (given by: top value - bottom value)
    private static final double BOTTOM_DIST_FROM_GROUND = inchesToMeters(9.0 + (4.0 / 8.0));
    private static final double UPPER_DIST_FROM_GROUND = inchesToMeters(74.75);
    private static final double TOTAL_DELTA_HEIGHT = UPPER_DIST_FROM_GROUND - BOTTOM_DIST_FROM_GROUND;
    private static final double MANUAL_MOVEMENT_DEADBAND = 0.02;
    private static final double MAX_RPM = 5000;
    private static final double POSITION_LOOP_KP = -5 * MAX_RPM; // (revolutions / minute) / (meter)
    private static final double POSITION_LOOP_KV = -3300; // (revolutions / minute) / (meter / second)
    private static final double MAXIMUM_ELEVATOR_MOTOR_TEMPERATURE = 80.0; // Celsius
    private static final double ZEROING_PERCENT_OUTPUT = 0.2;
    private static final PidfConstants ELEVATOR_NEO_PIDF_PARAMETERS = new PidfConstants(3E-5, 1E-6, 0.0, 0.0, 0.0);
    private static Elevator instance;
    private final DigitalInput bottomLimit;
    private final CANSparkMax motor;
    private final CANEncoder encoder;
    private final CANPIDController controller;
    private ElevatorPosition desiredPosition = ElevatorPosition.HATCH_LOW;
    private double height = 0.0;
    private double encoderPosition = 0.0;
    private double temperature = 0.0;
    private double desiredManualOutputPower = 0.0;
    private double encoderOffset = 0.0;
    private double currentErrorMeters = 0.0;
    private double lastReadTimestamp = Timer.getFPGATimestamp();
    private double currentVelocity = 0.0;
    private boolean hasZeroed = false;
    private boolean usePID = false;
    private boolean bottomLimitTriggered = false;

    private Elevator() {
        bottomLimit = new DigitalInput(2);
        motor = new CANSparkMax(ELEVATOR_NEO, CANSparkMaxLowLevel.MotorType.kBrushless);
        encoder = new CANEncoder(motor);
        controller = motor.getPIDController();
        controller.setP(ELEVATOR_NEO_PIDF_PARAMETERS.p);
        controller.setI(ELEVATOR_NEO_PIDF_PARAMETERS.i);
        controller.setD(ELEVATOR_NEO_PIDF_PARAMETERS.d);
        controller.setFF(ELEVATOR_NEO_PIDF_PARAMETERS.f);
        controller.setIZone(ELEVATOR_NEO_PIDF_PARAMETERS.iZone);
        controller.setOutputRange(-1.0, 1.0);
        // TODO test the following settings to reduce CAN bus utilization
        //  motor.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus0, 50);
        //  motor.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus1, 10);
        //  motor.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus2, 10);
    }

    public static Elevator getInstance() {
        if (instance == null) {
            instance = new Elevator();
        }
        return instance;
    }

    private double convertRawEncoderToRealHeight(double encoderValue) {
        return BOTTOM_DIST_FROM_GROUND + ((encoderValue + encoderOffset) / TOTAL_DELTA_ENCODER_VALUE) * TOTAL_DELTA_HEIGHT;
    }

    private double convertHeightToEncoder(double height) {
        return ((height - BOTTOM_DIST_FROM_GROUND) / TOTAL_DELTA_HEIGHT) * TOTAL_DELTA_ENCODER_VALUE - encoderOffset;
    }

    @Override
    public synchronized void readPeriodicInputs() {
        double timestamp = Timer.getFPGATimestamp();

        // If the bottom limit is hit, set the encoder offset and note that we have zeroed.
        bottomLimitTriggered = bottomLimit.get();
        encoderPosition = encoder.getPosition();
        if (bottomLimitTriggered) {
            if (!hasZeroed) {
                hasZeroed = true;
            }
            encoderOffset = -encoderPosition;
        }
        height = convertRawEncoderToRealHeight(encoderPosition);

        // Warn the drivers if the elevator motor temperature gets too high.
        temperature = motor.getMotorTemperature();
        if (temperature > MAXIMUM_ELEVATOR_MOTOR_TEMPERATURE) {
            DriverStation.reportError("ELEVATOR TEMPERATURE TOO HIGH", false);
        }

        // Calculate the current error (meters) and the velocity of the elevator
        double errorMeters = desiredPosition.getHeight() - height;
        // TODO this should probably just be encoder.getVelocity() converted to meters/second + a different status
        //  frame period, but we should
        //  double check the accuracy and sign of that vs the current method
        currentVelocity = (errorMeters - currentErrorMeters) / (timestamp - lastReadTimestamp);
        currentErrorMeters = errorMeters;
        lastReadTimestamp = timestamp;
    }

    @Override
    public void outputTelemetry() {
        double encoderFilteredPosition = encoderPosition + encoderOffset;
        double encVelocityMeasured = encoder.getVelocity();
        ELEVATOR_SHUFFLEBOARD.putNumber("Height (m)", height);
        ELEVATOR_SHUFFLEBOARD.putNumber("Height (in)", metersToInches(height));
        ELEVATOR_SHUFFLEBOARD.putNumber("Velocity (rev/min)", encoder.getVelocity());
        ELEVATOR_SHUFFLEBOARD.putNumber("Encoder (Raw)", encoderPosition);
        ELEVATOR_SHUFFLEBOARD.putNumber("Encoder (Filtered)", encoderFilteredPosition);
        ELEVATOR_SHUFFLEBOARD.putNumber("Current", motor.getOutputCurrent());
        ELEVATOR_SHUFFLEBOARD.putNumber("Desired Power", desiredManualOutputPower);
        ELEVATOR_SHUFFLEBOARD.putNumber("Temperature", temperature);
        ELEVATOR_SHUFFLEBOARD.putBoolean("Bottom Limit Triggered", bottomLimitTriggered);
        ELEVATOR_SHUFFLEBOARD.putBoolean("Use PID", usePID);
        // If using PID
        ELEVATOR_SHUFFLEBOARD.putNumber("Closed Loop/Error (m)", currentErrorMeters);
        ELEVATOR_SHUFFLEBOARD.putNumber("Closed Loop/Velocity via error (rev/min)", currentVelocity);
        // TODO remove this once we've figured out which method to use for getting velocity
        ELEVATOR_SHUFFLEBOARD.putNumber("Closed Loop/Velocity Differences", currentVelocity - encVelocityMeasured);
        ELEVATOR_SHUFFLEBOARD.putNumber("Closed Loop/Desired Height", desiredPosition.getHeight());
        ELEVATOR_SHUFFLEBOARD.putNumber("Closed Loop/Desired Encoder", convertHeightToEncoder(desiredPosition.getHeight()));
    }

    public synchronized void manualMovement(double manualPower) {
        usePID = false;
        desiredManualOutputPower = -manualPower;
    }

    public synchronized void pidToPosition(ElevatorPosition position) {
        usePID = true;
        desiredPosition = position;
    }

    @Override
    public synchronized void writePeriodicOutputs() {
        double value;
        ControlType controlType;
        if (!usePID) {
            desiredManualOutputPower = (Math.abs(desiredManualOutputPower) < MANUAL_MOVEMENT_DEADBAND) ? 0.0 : desiredManualOutputPower;
            value = desiredManualOutputPower;
            controlType = ControlType.kDutyCycle;
        } else {
            if (hasZeroed) {
                controlType = ControlType.kVelocity;
                value = (POSITION_LOOP_KP * currentErrorMeters) + (POSITION_LOOP_KV * currentVelocity);
                value = Math.abs(value) > MAX_RPM ? Math.copySign(MAX_RPM, value) : value;
            } else {
                controlType = ControlType.kDutyCycle;
                value = ZEROING_PERCENT_OUTPUT;
            }
        }
        if (bottomLimitTriggered && value >= -1.0E-05) {
            controlType = ControlType.kDutyCycle;
            value = 0;
        }
        controller.setReference(value, controlType);
    }

    @Override
    public synchronized void stop() {
        manualMovement(0.0);
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
