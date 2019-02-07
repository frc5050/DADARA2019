package frc.robot;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

@Deprecated
public class ReadASensorBot extends TimedRobot {
    private static final double UPPER_POT_VALUE = 2.25;
    private static final double LOWER_POT_VALUE = 4.35;
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
    private static final double SETPOINT_HEIGHT = 51.5 * 0.0254; // meters
    double voltage = 0.0;
    double height = 0.0;
    double encoderPosition = 0.0;
    double encoderFilteredPosition = 0.0;
    double temperature = 0.0;
    private CANSparkMax motor;
    private CANEncoder encoder;
    private CANPIDController controller;
    private Joystick joystick;
    private AnalogInput pot;
    private double power = 0.0;
    private double encoderOffset = 0.0;
    private double lastError = 0.0;

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
    public void robotInit() {
        pot = new AnalogInput(0);
        motor = new CANSparkMax(13, CANSparkMaxLowLevel.MotorType.kBrushless);
        encoder = new CANEncoder(motor);
        controller = motor.getPIDController();
        controller.setFF(HOLD_FEEDFORWARD);
        controller.setP(KP_ENCODER_BASED);
        controller.setI(0.0);
        controller.setD(0.0);
        controller.setIZone(0.0);
        joystick = new Joystick(0);

//        motor = new CANSparkMax(14, CANSparkMaxLowLevel.MotorType.kBrushed);
//        encoder = new CANEncoder(motor);
    }

    @Override
    public void robotPeriodic() {
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

        encoderFilteredPosition = encoderPosition + encoderOffset;
        SmartDashboard.putNumber("Pot", voltage);
        SmartDashboard.putNumber("Height", height);
        SmartDashboard.putNumber("Height (inches)", height * (1.0 / 0.0254));
        SmartDashboard.putNumber("Encoder (Raw)", encoderPosition);
        SmartDashboard.putNumber("Encoder (Filtered)", encoderFilteredPosition);
        SmartDashboard.putNumber("Encoder at 1.0 meters", convertHeightToEncoder(1.0));
        SmartDashboard.putNumber("Current", motor.getOutputCurrent());
        SmartDashboard.putNumber("Desired Power", power);
        SmartDashboard.putNumber("Temperature", temperature);
    }

    @Override
    public void disabledInit() {
    }

    @Override
    public void disabledPeriodic() {

    }

    @Override
    public void autonomousInit() {
    }

    @Override
    public void autonomousPeriodic() {

    }

    @Override
    public void teleopInit() {
    }

    @Override
    public void teleopPeriodic() {
        boolean manualOverride = joystick.getRawButton(1);
        boolean holdPosition = joystick.getRawButton(2);
        if (manualOverride) {
            power = -joystick.getRawAxis(1);
            power = (Math.abs(power) < 0.04) ? 0.0 : power;
        } else if (holdPosition) {
            power = HOLD_FEEDFORWARD;
        } else {
            double error = SETPOINT_HEIGHT - height;
            double velocity = lastError - error;
            power = (-error * KP_CUSTOM_PID) + (KV_CUSTOM_PID * velocity) + HOLD_FEEDFORWARD;
            lastError = error;
        }

        if (voltage > LOWER_POT_VALUE) {
            power = power > 0 ? 0 : power;
        } else if (voltage < UPPER_POT_VALUE) {
            power = power < 0 ? HOLD_FEEDFORWARD : power;
        }

        motor.set(power);
    }

    @Override
    public void testInit() {
    }

    @Override
    public void testPeriodic() {
    }

}
