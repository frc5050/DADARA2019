package frc.robot;

import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.ControlType;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;

import static frc.utils.Constants.ELEVATOR_NEO;

public class ElevatorBot extends TimedRobot {
    private final CANSparkMax motor = new CANSparkMax(ELEVATOR_NEO, CANSparkMaxLowLevel.MotorType.kBrushless);;
    private final CANPIDController controller = motor.getPIDController();;
    private final Joystick joystick0 = new Joystick(0);

    @Override
    public void robotInit() {
        controller.setP(0.0);
        controller.setI(0.0);
        controller.setD(0.0);
        controller.setFF(0.0);
        controller.setIZone(0.0);
        controller.setOutputRange(-1.0, 1.0);
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
        controller.setReference(joystick0.getRawAxis(1), ControlType.kDutyCycle);
    }

    @Override
    public void testInit() {
    }

    @Override
    public void testPeriodic() {
    }

}
