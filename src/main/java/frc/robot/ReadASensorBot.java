package frc.robot;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.*;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.utils.Constants;

public class ReadASensorBot extends TimedRobot {

    //    private CANSparkMax motor;
//    private CANEncoder encoder;
    private CANSparkMax motor;
    private CANEncoder encoder;
    private CANPIDController controller;
    private Joystick joystick;

    @Override
    public void robotInit() {
        motor = new CANSparkMax(13, CANSparkMaxLowLevel.MotorType.kBrushless);
        encoder = new CANEncoder(motor);
        controller = motor.getPIDController();
        controller.setFF(0.0);
        controller.setP(0.2);
        controller.setI(0.0);
        controller.setD(0.0);
        controller.setIZone(0.0);
        joystick = new Joystick(0);

//        motor = new CANSparkMax(14, CANSparkMaxLowLevel.MotorType.kBrushed);
//        encoder = new CANEncoder(motor);
    }

    @Override
    public void robotPeriodic() {
        SmartDashboard.putNumber("Encoder", encoder.getPosition());
        SmartDashboard.putNumber("Current", motor.getOutputCurrent());
        SmartDashboard.putNumber("Desired Power", joystick.getRawAxis(1));
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
        double power = -joystick.getRawAxis(1);
        power = (Math.abs(power) < 0.04) ? 0.0 : power;
        motor.set(power);
    }

    @Override
    public void testInit() {
    }

    @Override
    public void testPeriodic() {
    }

}
