package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

import static frc.utils.Constants.ROBOT_MAIN_SHUFFLEBOARD;

@Deprecated // TODO(Max) delete this whenever you're done with it
public class thing_a_ma_jing extends TimedRobot {
    private final Joystick joystick = new Joystick(0);
    private final WPI_TalonSRX leftdrive1 = new WPI_TalonSRX(15);
    private final WPI_VictorSPX leftdrive2 = new WPI_VictorSPX(0);
    private final WPI_TalonSRX rightdrive1 = new WPI_TalonSRX(2);
    private final WPI_VictorSPX rightdrive2 = new WPI_VictorSPX(3);
    private final SpeedControllerGroup leftdrive = new SpeedControllerGroup(leftdrive1, leftdrive2);
    private final SpeedControllerGroup rightdrive = new SpeedControllerGroup(rightdrive1, rightdrive2);
    private final DifferentialDrive drive = new DifferentialDrive(leftdrive, rightdrive);

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
