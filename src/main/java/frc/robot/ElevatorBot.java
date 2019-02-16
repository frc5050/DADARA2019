package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;

import static frc.utils.Constants.ELEVATOR_NEO;

public class ElevatorBot extends TimedRobot {

    private final CANSparkMax motor = new CANSparkMax(ELEVATOR_NEO, CANSparkMaxLowLevel.MotorType.kBrushless);;
    private final Joystick joystick0 = new Joystick(0);
    @Override
    public void robotInit() {

    }

    @Override
    public void robotPeriodic() {

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
        motor.set(joystick0.getRawAxis(1));
    }

    @Override
    public void testInit() {
    }

    @Override
    public void testPeriodic() {
    }

}
