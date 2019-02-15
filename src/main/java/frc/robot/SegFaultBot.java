package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;

import java.io.File;

/**
 * Just a {@link TimedRobot} that does nothing the whole time. Useful for getting baseline values for certain things.
 */
public class SegFaultBot extends TimedRobot {
    private File myFile;

    @Override
    public void robotInit() {
        try {
            myFile = new File("/home/lvuser/deploy/paths/LEVEL2_to_Rocket.pf1.csv");
            System.out.println("File is null: " + myFile == null);
            System.out.println("File path: " + myFile.getAbsolutePath());
            System.out.println("File path: " + myFile.getCanonicalPath());
        } catch (Exception e) {
            DriverStation.reportError("Read file", false);
        }
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
        Trajectory trajectory = Pathfinder.readFromCSV(myFile);
    }

    @Override
    public void testInit() {
    }

    @Override
    public void testPeriodic() {
    }

}
