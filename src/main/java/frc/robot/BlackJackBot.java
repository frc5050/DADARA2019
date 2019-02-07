package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import frc.subsystem.BlackJack;

@Deprecated
public class BlackJackBot extends TimedRobot {
    private BlackJack jack = BlackJack.getInstance();
    private Joystick joystick = new Joystick(0);

    @Override
    public void robotInit() {

    }

    @Override
    public void robotPeriodic() {
        jack.outputTelemetry();
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
        jack.setOpenLoop(-joystick.getRawAxis(1));
    }

    @Override
    public void testInit() {
    }

    @Override
    public void testPeriodic() {
    }

}
