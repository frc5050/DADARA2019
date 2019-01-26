package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import frc.loops.Looper;
import frc.subsystem.*;

import java.util.Arrays;

public class TmpRobot extends TimedRobot {
    private SubsystemManager subsystemManager = new SubsystemManager(
            Arrays.asList(
                    Drive.getInstance(),
                    HatchMechanism.getInstance(),
                    Cargo.getInstance(),
                    Elevator.getInstance()
//                    TmpElev.getInstance()
            )
    );
    private Looper enabledLooper = new Looper();
    private Looper disabledLooper = new Looper();
    private GameController gameController = GameController.getInstance();
//    private TmpElev elevator = TmpElev.getInstance();
    private Drive drive = Drive.getInstance();

    @Override
    public void robotInit() {
        subsystemManager.registerEnabledLoops(enabledLooper);
        subsystemManager.registerDisabledLoop(disabledLooper);
    }

    @Override
    public void disabledInit() {
        enabledLooper.stop();
        disabledLooper.start();
    }

    @Override
    public void teleopInit() {
        disabledLooper.stop();
        enabledLooper.start();
    }

    @Override
    public void teleopPeriodic() {
        drive.setOpenLoop(gameController.getDriveSignal());
//        elevator.setOpenLoop(gameController.elevateManual());
    }
}
