/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import frc.loops.Looper;
import frc.states.CargoState;
import frc.subsystem.*;
import frc.subsystem.test.DriveTest;
import frc.subsystem.test.SubsystemTest;

import java.util.Arrays;
import java.util.LinkedHashMap;

import static frc.utils.Constants.ROBOT_MAIN_SHUFFLEBOARD;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

    // TODO should we add manual tests?
    public enum Test {
        DEFAULT_TEST("None"),
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
            Drive.getInstance(),
            Cargo.getInstance(),
            Jacks.getInstance()
    ));

    private Drive drive = Drive.getInstance();
    private Cargo cargo = Cargo.getInstance();
    private Jacks jacks = Jacks.getInstance();

    private GameController gameController = GameController.getInstance();
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
        drive.setOpenLoop(gameController.getDriveSignal());

        if (gameController.cargoIntake()) {
            cargo.setDesiredState(CargoState.IntakeState.INTAKE);
        } else if (gameController.cargoOuttakeLeft()) {
            cargo.setDesiredState(CargoState.IntakeState.OUTTAKE_LEFT);
        } else if (gameController.cargoOuttakeRight()) {
            cargo.setDesiredState(CargoState.IntakeState.OUTTAKE_RIGHT);
        } else {
            cargo.setDesiredState(CargoState.IntakeState.STOPPED);
        }

        if (gameController.liftAllJacks()) {
            jacks.liftAll();
        } else if (gameController.retractFrontJack()) {
            jacks.retractFrontJack();
        } else {
            jacks.stop();
        }

        double timestamp = Timer.getFPGATimestamp();
        ROBOT_MAIN_SHUFFLEBOARD.putNumber("dt from TimedRobot", timestamp - this.previousTimestamp);
        this.previousTimestamp = timestamp;
    }

    // TODO add more tests
    // TODO automate test validation
    @Override
    public void testInit() {
        Robot.Test testSelcted = tests.get(chooser.getSelected());
        disabledLooper.stop();
        enabledLooper.stop();
        switch (testSelcted) {
            case DEFAULT_TEST:
                subsystemTest = null;
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
//        Drive.getInstance().outputTelemetry();
        ROBOT_MAIN_SHUFFLEBOARD.putBoolean("DPAD_DOWN", gameController.cargoIntake());
        ROBOT_MAIN_SHUFFLEBOARD.putBoolean("DPAD_UP", gameController.cargoOuttakeFront());
        ROBOT_MAIN_SHUFFLEBOARD.putBoolean("DPAD_RIGHT", gameController.cargoOuttakeRight());
        ROBOT_MAIN_SHUFFLEBOARD.putBoolean("DPAD_LEFT", gameController.cargoOuttakeLeft());
        // TODO test subsystemManager.outputTelemetry();
    }

}
