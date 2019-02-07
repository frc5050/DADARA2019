package frc.subsystem.deprecated;

import frc.subsystem.Drive;
import frc.subsystem.Subsystem;

import static frc.utils.Constants.ROBOT_STATE_SHUFFLEBOARD;
// Creates subsystem variables and allows for use of instances in other functions
@Deprecated // we never really used this, it can be easily recreated and actually implemented if desired at later date
public class RobotState extends Subsystem {
    // TODO actually implement an odometry system. ORB-SLAM/SVO/Simple Wheel odometry might be useful?
    //  adding an extra notifier just for this might be worth it if it proves useful
    private static RobotState instance;

    private Drive drive = Drive.getInstance();
    private PeriodicIO periodicIo = new PeriodicIO();

    private RobotState() {

    }

    public static RobotState getInstance() {
        if (instance != null) {
            instance = new RobotState();
        }
        return instance;
    }

    @Override
    public synchronized void readPeriodicInputs() {
        periodicIo.yaw = drive.getYaw();
        periodicIo.roll = drive.getRoll();
        periodicIo.pitch = drive.getPitch();
    }

    @Override
    public void outputTelemetry() {
        ROBOT_STATE_SHUFFLEBOARD.putNumber("Yaw", periodicIo.yaw);
        ROBOT_STATE_SHUFFLEBOARD.putNumber("Roll", periodicIo.roll);
        ROBOT_STATE_SHUFFLEBOARD.putNumber("Pitch", periodicIo.pitch);
    }

    @Override
    public void stop() {

    }

    public synchronized double getYaw() {
        return periodicIo.yaw;
    }

    public synchronized double getRoll() {
        return periodicIo.roll;
    }

    public synchronized double getPitch() {
        return periodicIo.pitch;
    }

    private static class PeriodicIO {
        double yaw;
        double roll;
        double pitch;
    }
}