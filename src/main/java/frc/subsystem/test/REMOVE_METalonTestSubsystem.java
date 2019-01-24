package frc.subsystem.test;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.ControlType;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.subsystem.Subsystem;

public class REMOVE_METalonTestSubsystem extends Subsystem {

    private WPI_TalonSRX m_motor;
    private Joystick m_stick;
    private double encoderPos = 0.0;
    private double motorDemand = 0.0;
    private double timestamp = 0.0;
    private final double initTimestamp;
    private double dt = 0.0;

    public REMOVE_METalonTestSubsystem() {
        m_motor = new WPI_TalonSRX(7);
        initTimestamp = Timer.getFPGATimestamp();
    }

    @Override
    public synchronized void readPeriodicInputs() {
        double currentTime = Timer.getFPGATimestamp();
        dt = timestamp - currentTime;
        timestamp = currentTime;
        motorDemand = Math.sin((currentTime - initTimestamp) * 1.3) / 4 + .5;
    }

    @Override
    public synchronized void writePeriodicOutputs() {
        m_motor.set(motorDemand);
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
