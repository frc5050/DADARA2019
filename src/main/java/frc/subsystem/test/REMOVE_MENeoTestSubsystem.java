package frc.subsystem.test;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.ControlType;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.subsystem.Subsystem;

public class REMOVE_MENeoTestSubsystem extends Subsystem {

    private CANSparkMax m_motor;
    private CANEncoder m_encoder;
    private Joystick m_stick;
    private double encoderPos = 0.0;
    private double motorDemand = 0.0;
    private double timestamp = 0.0;
    private final double initTimestamp;
    private double dt = 0.0;

    public REMOVE_MENeoTestSubsystem() {
        m_motor = new CANSparkMax(14, CANSparkMaxLowLevel.MotorType.kBrushless);
        m_motor.setCANTimeout(0);
        m_encoder = m_motor.getEncoder();
        initTimestamp = Timer.getFPGATimestamp();
    }

    @Override
    public synchronized void readPeriodicInputs() {
        double currentTime = Timer.getFPGATimestamp();
        dt = timestamp - currentTime;
        timestamp = currentTime;
        encoderPos = m_encoder.getPosition();
        motorDemand = Math.sin((currentTime - initTimestamp) * 1.3);
        m_motor.getPIDController().setP(1);
        m_motor.getPIDController().setD(0);
        m_motor.getPIDController().setI(0);
        m_motor.getPIDController().setIZone(0);
        m_motor.getPIDController().setFF(0);
    }

    @Override
    public synchronized void writePeriodicOutputs() {
        m_motor.getPIDController().setReference(motorDemand * 10.0, ControlType.kVoltage);
    }

    @Override
    public synchronized void outputTelemetry() {
        SmartDashboard.putNumber("Encoder Value", encoderPos);
        SmartDashboard.putNumber("Motor Demand", motorDemand);
        SmartDashboard.putNumber("Loop time", dt * 100 * 100);
    }

    @Override
    public void stop() {

    }
}
