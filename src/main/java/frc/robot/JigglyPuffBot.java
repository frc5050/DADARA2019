package frc.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.zip.DeflaterInputStream;

public class JigglyPuffBot extends TimedRobot {

    private DigitalInput dio2= new DigitalInput(2);
    private DigitalInput dio1 = new DigitalInput(1);
    private DigitalInput dio0 = new DigitalInput(3);

    @Override
    public void robotInit() {
    }

    @Override
    public void robotPeriodic() {
        SmartDashboard.putBoolean("Dio 2", dio2.get());
        SmartDashboard.putBoolean("Dio 1", dio1.get());
        SmartDashboard.putBoolean("Dio 0", dio0.get());
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

