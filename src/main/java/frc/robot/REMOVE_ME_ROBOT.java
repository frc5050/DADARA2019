package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import frc.loops.Looper;
import frc.subsystem.SubsystemManager;
import frc.subsystem.test.REMOVE_MENeoTestSubsystem;

import java.util.Arrays;

/**
 * This is a demo program showing the use of the RobotDrive class, specifically
 * it contains the code necessary to operate a robot with tank drive.
 */
public class REMOVE_ME_ROBOT extends TimedRobot {
    private static final int deviceID = 1;
    private Joystick m_stick;
    private Looper enabledLooper = new Looper();
    private Looper disabledLooper = new Looper();
    private SubsystemManager manager = new SubsystemManager(Arrays.asList(new REMOVE_MENeoTestSubsystem()));
    private double lastStablePosition = 0.0;

    @Override
    public void robotInit() {
        manager.registerDisabledLoop(disabledLooper);
        manager.registerEnabledLoops(enabledLooper);
    }

    @Override
    public void teleopInit() {
        disabledLooper.stop();
        enabledLooper.start();
    }

    @Override
    public void teleopPeriodic() {
        manager.outputTelemetry();
    }
}