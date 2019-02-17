package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import frc.utils.DriveHelper;
import frc.utils.DriveSignal;

import static frc.utils.Constants.*;

public class SimpleRobot extends TimedRobot {
    private final CANSparkMax leftMaster = new CANSparkMax(LEFT_DRIVE_1, CANSparkMaxLowLevel.MotorType.kBrushless);
    private final CANSparkMax leftSlave = new CANSparkMax(16, CANSparkMaxLowLevel.MotorType.kBrushless);
    private final CANSparkMax rightMaster = new CANSparkMax(RIGHT_DRIVE_1, CANSparkMaxLowLevel.MotorType.kBrushless);
    private final CANSparkMax rightSlave = new CANSparkMax(RIGHT_DRIVE_2, CANSparkMaxLowLevel.MotorType.kBrushless);
    private final Joystick joystick = new Joystick(0);

    @Override
    public void robotInit() {
        leftSlave.follow(leftMaster);
        rightSlave.follow(rightMaster);
        rightMaster.setInverted(true);
        rightSlave.setInverted(true);
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
        DriveSignal driveSignal = DriveHelper.arcadeToDriveSignal(-joystick.getRawAxis(1), joystick.getRawAxis(0));
        leftMaster.set(driveSignal.getLeftOutput());
//        leftSlave.set(driveSignal.getLeftOutput());
        rightMaster.set(driveSignal.getRightOutput());
//        rightSlave.set(driveSignal.getRightOutput());
    }

    @Override
    public void testInit() {
    }

    @Override
    public void testPeriodic() {
    }

}
