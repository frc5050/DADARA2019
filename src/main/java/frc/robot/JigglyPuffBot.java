package frc.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Test robot for just reading sensor inputs and not doing anything else. Should be removed by production, but helpful
 * for the moment.
 */
public class JigglyPuffBot extends TimedRobot {

    private final DigitalInput dio0 = new DigitalInput(0);
    private final DigitalInput dio1 = new DigitalInput(1);
    private final DigitalInput dio2 = new DigitalInput(2);
    private final DigitalInput dio3 = new DigitalInput(3);
    private final DigitalInput dio4 = new DigitalInput(4);

    @Override
    public void robotInit() {
    }

    @Override
    public void robotPeriodic() {
        SmartDashboard.putBoolean("Dio 0", dio0.get());
        SmartDashboard.putBoolean("Dio 2", dio2.get());
        SmartDashboard.putBoolean("Dio 1", dio1.get());
        SmartDashboard.putBoolean("Dio 3", dio3.get());
        SmartDashboard.putBoolean("Dio 4", dio4.get());
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
    }

    @Override
    public void testInit() {
    }

    @Override
    public void testPeriodic() {
    }

}

