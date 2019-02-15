package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

import static frc.utils.Constants.ROBOT_MAIN_SHUFFLEBOARD;

/**
 * Just a {@link TimedRobot} that does nothing the whole time. Useful for getting baseline values for certain things.
 */
public class thing_a_ma_jing extends TimedRobot {

    final Joystick joystick = new Joystick(0);
        final WPI_TalonSRX leftdrive1 = new WPI_TalonSRX(15);
        final WPI_VictorSPX leftdrive2 = new WPI_VictorSPX(0);
        final WPI_TalonSRX rightdrive1 = new WPI_TalonSRX(2);
        final WPI_VictorSPX rightdrive2 = new WPI_VictorSPX(3);
        final SpeedControllerGroup leftdrive = new SpeedControllerGroup(leftdrive1, leftdrive2);
        final SpeedControllerGroup rightdrive = new SpeedControllerGroup(rightdrive1, rightdrive2);
        final DifferentialDrive drive = new DifferentialDrive(leftdrive, rightdrive);

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
        drive.arcadeDrive(-joystick.getRawAxis(1), joystick.getRawAxis(0));
        //drive.curvatureDrive(-joystick.getRawAxis(1), joystick.getRawAxis(0), joystick.getRawAxis(0) >= 0.75 || joystick.getRawAxis(0) <= -0.75);
        ROBOT_MAIN_SHUFFLEBOARD.putNumber("leftmotor", leftdrive.get());
        ROBOT_MAIN_SHUFFLEBOARD.putNumber("rightmotor", rightdrive.get());

    }

    @Override
    public void testInit() {
    }

    @Override
    public void testPeriodic() {
    }

}
