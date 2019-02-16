package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.TimedRobot;

import static frc.utils.Constants.ELEVATOR_NEO;

public class ElevatorBot extends TimedRobot {

    private final CANSparkMax motor = new CANSparkMax(ELEVATOR_NEO, CANSparkMaxLowLevel.MotorType.kBrushless);;

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
        motor
    }

    @Override
    public void testInit() {
    }

    @Override
    public void testPeriodic() {
    }

}
