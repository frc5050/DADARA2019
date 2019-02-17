package frc.subsystem;

import frc.utils.Constants;
import frc.utils.DriveSignal;
import jaci.pathfinder.Trajectory;

public abstract class DriveTrain extends Subsystem {
    private static DriveTrain instance;

    public static DriveTrain getInstance() {
        if (instance == null) {
            switch (Constants.ROBOT) {
                case A_BOT:
                    instance = NeoDrive.getInstance();
                    break;
                case B_BOT:
                    instance = DriveTrain.getInstance();
                    break;
                default:
                    instance = NeoDrive.getInstance();
            }
        }
        return instance;
    }

    public abstract void setBrakeMode(boolean brake);

    public abstract void setOpenLoop(DriveSignal signal);

    public abstract void setTrajectory(Trajectory trajectory, boolean invert);

    public abstract boolean isDone();

    public abstract void setHeading(double heading);

    public abstract void readPeriodicInputs();

    public abstract double getYaw();

    public abstract double getPitch();

    public abstract double getRoll();

    public abstract double getYawRaw();

    public abstract double getPitchRaw();

    public abstract double getRollRaw();

    public abstract void resetNavX();

}