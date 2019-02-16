package frc.autonomous;

import java.io.File;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.subsystem.Drive;
import frc.subsystem.Elevator;
import frc.utils.DriveSignal;
import frc.subsystem.Hatch;
import frc.subsystem.Elevator.ElevatorPosition;
import jaci.pathfinder.*;

public class Lvl2RightCloseRKT extends AutoBase {
    private final Drive drive = Drive.getInstance();
    private final Hatch hatch = Hatch.getInstance();
    private final Elevator elevator = Elevator.getInstance();
    private State state = State.INIT;

    @Override
    public void init() {

    }

    @Override
    public void periodic(double timestamp) {
        SmartDashboard.putString("Autostate", state.toString());
        switch (state) {
            case INIT:
                System.out.println(state);
                state = State.LEVEL2_to_Rocket;
                drive.setTrajectory(loadTrajectory("/home/lvuser/deploy/paths/LEVEL2_to_Rocket.pf1.csv"));
                elevator.pidToPosition(ElevatorPosition.HATCH_LOW);
                break;
            case LEVEL2_to_Rocket:
                // hatch.setHatchPlace();
                System.out.println(state);
                if (drive.isDone()){
                    state = State.Right_RKT_Close_Backup;
                    drive.setTrajectory(loadTrajectory("/home/lvuser/deploy/paths/Right_RKT_Close_Backup.pf1.csv"));
                }
                break;
            case Right_RKT_Close_Backup:
                System.out.println(state);
                hatch.setHatchPull();
                if (drive.isDone()){
                    state = State.Close_Right_Rkt_to_FEED;
                    drive.setTrajectory(loadTrajectory("/home/lvuser/deploy/paths/Close_Right_Rkt_to_FEED.pf1.csv"));
                }
                break;
            //180 degree turn
            case Close_Right_Rkt_to_FEED:
                System.out.println(state);
                if (drive.isDone()){
                    state = State.STOP;
            }
                break;
            case STOP:
                System.out.println(state);
//                elevator.pidToPosition(Elevator.ElevatorPosition.HATCH_LOW);
                drive.setOpenLoop(DriveSignal.NEUTRAL);
                break;
        }
    }

    @Override
    public boolean isDone() {
        return state == State.STOP;
    }

    public static Trajectory loadTrajectory(String path) {
        return Pathfinder.readFromCSV(new File(path));
    }

    private enum State {
        INIT,
        LEVEL2_to_Rocket,
        Right_RKT_Close_Backup,
        Close_Right_Rkt_to_FEED,
        STOP
    }
}