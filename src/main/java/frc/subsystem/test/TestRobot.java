package frc.subsystem.test;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import frc.loops.Looper;
import frc.subsystem.Drive;
import frc.subsystem.SubsystemManager;

import java.util.Arrays;
import java.util.LinkedHashMap;

public class TestRobot extends TimedRobot {



    // TODO should we add manual tests?
    public enum Test {
        DEFAULT_TEST("None"),
        GAMEPAD_TEST("Drive Automated Test"),
        DRIVE_TEST("Drive Automated Test"),
        //        DRIVE_MANUAL_CONTROL_TEST("Drive Manual Test"),
        CARGO_TEST("Cargo Automated Test"),
        //        CARGO_MANUAL_TEST("Cargo Manual Test"),
        ELEVATOR_TEST("Elevator Automated Test"),
        //        ELEVATOR_MANUAL_TEST("Elevator Manual Test"),
        JACKS_TEST("Jacks Test"),
        //        JACKS_MANUAL_TEST("Jacks Manual Test"),
        ROBOT_STATE_TEST("Robot State Test");
//        ROBOT_STATE_MANUAL_TEST("Robot State Manual Test");

        private String option;

        Test(String option) {
            this.option = option;
        }

        public String getOption() {
            return option;
        }
    }


    private final SendableChooser<String> chooser = new SendableChooser<>();

    private Looper enabledLooper = new Looper();
    private Looper disabledLooper = new Looper();
    private final SubsystemManager subsystemManager = new SubsystemManager(Arrays.asList(
            Drive.getInstance()
    ));

    private LinkedHashMap<String, Test> tests = new LinkedHashMap<>();

    @Override
    public void robotInit() {
        tests.put(Test.DEFAULT_TEST.getOption(), Test.DEFAULT_TEST);
        for (Test test : Test.values()) {
            if (test != Test.DEFAULT_TEST) {
                tests.put(test.getOption(), test);
            }
        }
        chooser.setDefaultOption(Test.DEFAULT_TEST.getOption(), Test.DEFAULT_TEST.getOption());
        for (String s : tests.keySet()) {
            chooser.addOption(s, s);
        }

        subsystemManager.registerEnabledLoops(enabledLooper);
        subsystemManager.registerDisabledLoop(disabledLooper);
    }

    @Override
    public void robotPeriodic() {
        outputTelemetry();
    }

    @Override
    public void disabledInit() {
        enabledLooper.stop();
        disabledLooper.start();
    }

    @Override
    public void autonomousInit() {
        disabledLooper.stop();
        enabledLooper.start();
    }

    @Override
    public void autonomousPeriodic() {

    }

    @Override
    public void teleopInit() {
        disabledLooper.stop();
        enabledLooper.start();
    }

    @Override
    public void teleopPeriodic() {
    }

    // TODO add more tests
    // TODO automate test validation
    @Override
    public void testInit() {
        Test testSelcted = tests.get(chooser.getSelected());
        disabledLooper.stop();
        enabledLooper.stop();
        switch (testSelcted) {
            case DEFAULT_TEST:
                subsystemTest = null;
                break;
            case GAMEPAD_TEST:
                XboxController xboxController = new XboxController(1);
                break;
            case DRIVE_TEST:
                subsystemTest = new DriveTest();
                break;
            case CARGO_TEST:
                subsystemTest = null;
                break;
            case ELEVATOR_TEST:
                subsystemTest = null;
                break;
            case JACKS_TEST:
                subsystemTest = null;
                break;
            case ROBOT_STATE_TEST:
                subsystemTest = null;
                break;
        }
    }

    private double previousTimestamp = Timer.getFPGATimestamp();

    private static final String TEST_INFORMATION_PREFIX = "Test Information";
    private SubsystemManager testSubsystemManager;
    private Test lastTest;
    private SubsystemTest subsystemTest;

    @Override
    public void testPeriodic() {

    }

    public void outputTelemetry() {
        enabledLooper.outputTelemetry();
    }

    private void driveTestInit(){

    }

    private void driveTestPeriodic(){

    }

}
