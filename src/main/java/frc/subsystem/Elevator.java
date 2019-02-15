package frc.subsystem;

import com.revrobotics.*;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import frc.utils.PidfConstants;

import static frc.utils.Constants.*;
import static frc.utils.UnitConversions.inchesToMeters;
import static frc.utils.UnitConversions.metersToInches;

public final class Elevator extends Subsystem {
    private static final double TOTAL_DELTA_ENCODER_VALUE = -181.22; // revolutions (given by: top value - bottom value)
    private static final double BOTTOM_DIST_FROM_GROUND = inchesToMeters(9.0 + (4.0 / 8.0));
    private static final double UPPER_DIST_FROM_GROUND = inchesToMeters(74.75);
    private static final double TOTAL_DELTA_HEIGHT = UPPER_DIST_FROM_GROUND - BOTTOM_DIST_FROM_GROUND;
    private static final double MANUAL_MOVEMENT_DEADBAND = 0.02;
    private static final double MAX_RPM = 5000.0;
    private static final double POSITION_LOOP_KP = -5.0; // (revolutions / minute) / (meter)
    private static final double POSITION_LOOP_KV = -0.66; // (revolutions / minute) / (meter / second)
    private static final double MAXIMUM_ELEVATOR_MOTOR_TEMPERATURE = 80.0; // Celsius
    private static final double ZEROING_PERCENT_OUTPUT = 0.2;
    private static final PidfConstants ELEVATOR_NEO_PIDF_PARAMETERS = new PidfConstants(3.0E-05, 1.0E-06, 0.0, 0.0, 0.0);
    private static Elevator instance;
    private final DigitalInput bottomLimit;
    private final CANSparkMax motor;
    private final CANEncoder encoder;
    private final CANPIDController controller;
    private ElevatorPosition desiredPosition = ElevatorPosition.HATCH_LOW;
    private PeriodicIo periodicIo = new PeriodicIo();

    /**
     * Constructor.
     */
    private Elevator() {
        bottomLimit = new DigitalInput(ELEVATOR_BOTTOM_LIMIT);
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

    /**
     * Returns a static instance of the {@link Elevator} subsystem. If none has been created yet, the instance is created.
     * This enables multiple other subsystems and any other classes to use this class without having to pass an instance
     * or take the risk of trying to instantiate multiple instances of this class, which would result in errors.
     *
     * @return a static instance of the {@link Elevator} subsystem.
     */
    public static Elevator getInstance() {
        if (instance == null) {
            instance = new Elevator();
        }
        return instance;
    }

    /**
     * Converts a filtered encoder value to a real height in meters.
     *
     * @param encoderValueFiltered the filtered encoder value, that is, the raw encoder value with the zero point taken
     *                             into account.
     * @return the real height of the bottom of the carriage from the ground, in meters.
     */
    private static double convertFilteredEncoderToRealHeight(double encoderValueFiltered) {
        return BOTTOM_DIST_FROM_GROUND + (encoderValueFiltered / TOTAL_DELTA_ENCODER_VALUE) * TOTAL_DELTA_HEIGHT;
    }

    /**
     * Converts a filtered encoder value to a real height in meters.
     *
     * @param height the real height of the bottom of the carriage from the ground, in meters.
     * @return the filtered encoder value at the specified height, that is, the raw encoder value with the zero point
     * taken into account.
     */
    private static double convertHeightToFilteredEncoder(double height) {
        return ((height - BOTTOM_DIST_FROM_GROUND) / TOTAL_DELTA_HEIGHT) * TOTAL_DELTA_ENCODER_VALUE;
    }

    @Override
    public synchronized void readPeriodicInputs() {
        double timestamp = Timer.getFPGATimestamp();

        // If the bottom limit is hit, set the encoder offset and note that we have zeroed.
        periodicIo.bottomLimitTriggered = bottomLimit.get();
        periodicIo.encoderPosition = encoder.getPosition();
        if (periodicIo.bottomLimitTriggered) {
            if (!periodicIo.hasZeroed) {
                periodicIo.hasZeroed = true;
            }
            periodicIo.encoderOffset = -periodicIo.encoderPosition;
        }
        periodicIo.height = convertFilteredEncoderToRealHeight(periodicIo.encoderPosition + periodicIo.encoderOffset);

        // Warn the drivers if the elevator motor temperature gets too high.
        periodicIo.temperature = motor.getMotorTemperature();
        if (periodicIo.temperature > MAXIMUM_ELEVATOR_MOTOR_TEMPERATURE) {
            DriverStation.reportError("ELEVATOR TEMPERATURE TOO HIGH", false);
        }

        // Calculate the current error (meters) and the velocity of the elevator
        double errorMeters = desiredPosition.getHeight() - periodicIo.height;
        // TODO this should probably just be encoder.getVelocity() converted to meters/second + a different status
        //  frame period, but we should
        //  double check the accuracy and sign of that vs the current method
        periodicIo.currentVelocity = (errorMeters - periodicIo.currentErrorMeters) / (timestamp - periodicIo.lastReadTimestamp);
        periodicIo.currentErrorMeters = errorMeters;
        periodicIo.lastReadTimestamp = timestamp;
    }

    @Override
    public synchronized void writePeriodicOutputs() {
        double value;
        ControlType controlType;

        if (!periodicIo.usePID) {
            periodicIo.desiredManualOutputPower = (Math.abs(periodicIo.desiredManualOutputPower) < MANUAL_MOVEMENT_DEADBAND) ? 0.0 : periodicIo.desiredManualOutputPower;
            value = periodicIo.desiredManualOutputPower;
            controlType = ControlType.kDutyCycle;
        } else if (periodicIo.hasZeroed) {
            controlType = ControlType.kVelocity;
            value = ((POSITION_LOOP_KP * periodicIo.currentErrorMeters) + (POSITION_LOOP_KV * periodicIo.currentVelocity)) * MAX_RPM;
            value = Math.abs(value) > MAX_RPM ? Math.copySign(MAX_RPM, value) : value;
        } else {
            controlType = ControlType.kDutyCycle;
            value = ZEROING_PERCENT_OUTPUT;
        }

        if (periodicIo.bottomLimitTriggered && value >= -1.0E-05) {
            controlType = ControlType.kDutyCycle;
            value = 0;
        }
        controller.setReference(value, controlType);
    }

    @Override
    public void outputTelemetry() {
        double encoderFilteredPosition = periodicIo.encoderPosition + periodicIo.encoderOffset;
        double encVelocityMeasured = encoder.getVelocity();
        ELEVATOR_SHUFFLEBOARD.putNumber("Height (m)", periodicIo.height);
        ELEVATOR_SHUFFLEBOARD.putNumber("Height (in)", metersToInches(periodicIo.height));
        ELEVATOR_SHUFFLEBOARD.putNumber("Velocity (rev/min)", encVelocityMeasured);
        ELEVATOR_SHUFFLEBOARD.putNumber("Encoder (Raw)", periodicIo.encoderPosition);
        ELEVATOR_SHUFFLEBOARD.putNumber("Encoder (Filtered)", encoderFilteredPosition);
        ELEVATOR_SHUFFLEBOARD.putNumber("Current", motor.getOutputCurrent());
        ELEVATOR_SHUFFLEBOARD.putNumber("Desired Power", periodicIo.desiredManualOutputPower);
        ELEVATOR_SHUFFLEBOARD.putNumber("Temperature", periodicIo.temperature);
        ELEVATOR_SHUFFLEBOARD.putBoolean("Bottom Limit Triggered", periodicIo.bottomLimitTriggered);
        ELEVATOR_SHUFFLEBOARD.putBoolean("Use PID", periodicIo.usePID);
        // If using PID
        ELEVATOR_SHUFFLEBOARD.putNumber("Closed Loop/Error (m)", periodicIo.currentErrorMeters);
        ELEVATOR_SHUFFLEBOARD.putNumber("Closed Loop/Velocity via error (rev/min)", periodicIo.currentVelocity);
        // TODO remove this once we've figured out which method to use for getting velocity
        ELEVATOR_SHUFFLEBOARD.putNumber("Closed Loop/Velocity Differences", periodicIo.currentVelocity - encVelocityMeasured);
        ELEVATOR_SHUFFLEBOARD.putNumber("Closed Loop/Desired Height", desiredPosition.getHeight());
        final double desiredEncoderPositionFiltered = convertHeightToFilteredEncoder(desiredPosition.getHeight());
        ELEVATOR_SHUFFLEBOARD.putNumber("Closed Loop/Desired Encoder Filtered", desiredEncoderPositionFiltered);
        ELEVATOR_SHUFFLEBOARD.putNumber("Closed Loop/Desired Encoder Raw", desiredEncoderPositionFiltered - periodicIo.encoderOffset);
    }

    @Override
    public synchronized void stop() {
        manualMovement(0.0);
    }

    /**
     * Writes the given duty cycle to the motor as an override for positional control. Does not require prior zeroing
     * and does not use any closed loop control.
     *
     * @param dutyCycle the duty cycle to write to the motor on the range [-1.0, 1.0]
     */
    public synchronized void manualMovement(double dutyCycle) {
        periodicIo.usePID = false;
        periodicIo.desiredManualOutputPower = -dutyCycle;
    }

    /**
     * Sets the elevator to try and automatically move to a given position. Note that this function does not need to be
     * called continuously, and can just be called whenever there is a desire for a change in the desired position
     * of the elevator.
     *
     * @param position the {@link ElevatorPosition} to try to move to.
     */
    public synchronized void pidToPosition(ElevatorPosition position) {
        periodicIo.usePID = true;
        desiredPosition = position;
    }

    /**
     * The positions that the elevator can be set to automatically go to.
     */
    public enum ElevatorPosition {
        HATCH_LOW(BOTTOM_DIST_FROM_GROUND),
        HATCH_MID(inchesToMeters(38)),
        HATCH_HIGH(inchesToMeters(64 + 1)),
        CARGO_LOW(inchesToMeters(21)),
        CARGO_MID(inchesToMeters(49.25)),
        CARGO_HIGH(inchesToMeters(73 + 0.25));

        private final double height;

        ElevatorPosition(final double height) {
            this.height = height;
        }

        double getHeight() {
            return height;
        }
    }

    private static class PeriodicIo {
        // Input
        double height;
        double encoderPosition;
        double temperature;
        double encoderOffset;
        double currentErrorMeters;
        double lastReadTimestamp;
        double currentVelocity;
        boolean hasZeroed = false;
        boolean bottomLimitTriggered = false;

        // Output
        double desiredManualOutputPower;
        boolean usePID = false;
    }
}
