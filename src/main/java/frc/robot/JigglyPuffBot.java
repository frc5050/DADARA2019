package frc.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.zip.DeflaterInputStream;

public class JigglyPuffBot extends TimedRobot {

    private DigitalInput dio0 = new DigitalInput(0);
    private DigitalInput dio1 = new DigitalInput(1);
    private DigitalInput dio2= new DigitalInput(2);
    private DigitalInput dio3 = new DigitalInput(3);
    private AnalogInput analogInput = new AnalogInput(0);

    @Override
    public void robotInit() {
    }

    @Override
    public void robotPeriodic() {
        double rangeCm = analogInput.getVoltage() / 2.0;
        double rangeIn = rangeCm / 2.54;
        SmartDashboard.putNumber("Range (cm)", rangeCm);
        SmartDashboard.putNumber("Range (in)", rangeIn);
        SmartDashboard.putBoolean("Dio 0", dio0.get());
        SmartDashboard.putBoolean("Dio 2", dio2.get());
        SmartDashboard.putBoolean("Dio 1", dio1.get());
        SmartDashboard.putBoolean("Dio 3", dio3.get());
    }

    @Override
    public void disabledInit() {
    }

    @Override
    public void disabledPeriodic(){

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
    }

    @Override
    public void testInit() {
    }

    @Override
    public void testPeriodic() {
    }

}

