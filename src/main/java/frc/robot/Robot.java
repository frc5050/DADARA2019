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
import frc.subsystem.Cargo;
import frc.subsystem.Drive;
import frc.subsystem.Jacks;
import frc.subsystem.SubsystemManager;
import frc.subsystem.test.CargoTest;
import frc.subsystem.test.DriveTest;
import frc.subsystem.test.GamepadTest;
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
    private final SendableChooser<String> testChooser = new SendableChooser<>();
    private final SubsystemManager subsystemManager = new SubsystemManager(Arrays.asList(
            Drive.getInstance(),
            Cargo.getInstance(),
            Jacks.getInstance()
    ));
    private LinkedHashMap<String, Test> tests = new LinkedHashMap<>();
    private Looper enabledLooper = new Looper();
    private Looper disabledLooper = new Looper();
    private GameController gameController = GameController.getInstance();
    private Drive drive = Drive.getInstance();
    private Cargo cargo = Cargo.getInstance();
    private Jacks jacks = Jacks.getInstance();

    private double previousTimestamp = Timer.getFPGATimestamp();
    private SubsystemTest subsystemTest;

    @Override
    public void robotInit() {
        tests.put(Test.DEFAULT_TEST.getOption(), Test.DEFAULT_TEST);
        testChooser.setDefaultOption(Test.DEFAULT_TEST.getOption(), Test.DEFAULT_TEST.getOption());

        for (Test test : Test.values()) {
            if (test != Test.DEFAULT_TEST) {
                tests.put(test.getOption(), test);
            }
        }

        for (String testKey : tests.keySet()) {
            testChooser.addOption(testKey, testKey);
        }

        subsystemManager.registerEnabledLoops(enabledLooper);
        subsystemManager.registerDisabledLoop(disabledLooper);
    }

    @Override
    public void robotPeriodic() {
        outputTelemetry();
        double timestamp = Timer.getFPGATimestamp();
        ROBOT_MAIN_SHUFFLEBOARD.putNumber("dt from TimedRobot", timestamp - this.previousTimestamp);
        previousTimestamp = timestamp;
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

        if(gameController.liftJack()){
            jacks.automaticSyncLiftBasic();
        }
    }

    // TODO add more tests
    // TODO automate test validation
    @Override
    public void testInit() {
        Test testSelected = tests.get(testChooser.getSelected());
        disabledLooper.stop();
        enabledLooper.stop();
        switch (testSelected) {
            case DEFAULT_TEST:
                subsystemTest = null;
                break;
            case GAMEPAD_TEST:
                subsystemTest = new GamepadTest();
                break;
            case DRIVE_TEST:
                subsystemTest = new DriveTest();
                break;
            case CARGO_TEST:
                subsystemTest = new CargoTest();
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

    @Override
    public void testPeriodic() {
        subsystemTest.periodic(Timer.getFPGATimestamp());
    }

    public void outputTelemetry() {
        enabledLooper.outputTelemetry();
    }

    public enum Test {
        DEFAULT_TEST("None"),
        GAMEPAD_TEST("Gamepad Automated Test"),
        DRIVE_TEST("Drive Automated Test"),
        CARGO_TEST("Cargo Automated Test"),
        ELEVATOR_TEST("Elevator Automated Test"),
        JACKS_TEST("Jacks Test"),
        ROBOT_STATE_TEST("Robot State Test");

        private String option;

        Test(String option) {
            this.option = option;
        }

        public String getOption() {
            return option;
        }
    }

}
