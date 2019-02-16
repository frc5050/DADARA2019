/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import frc.autonomous.AutoBase;
import frc.inputs.GameController;
import frc.loops.Looper;
import frc.states.IntakeState;
import frc.subsystem.*;
import frc.subsystem.test.CargoTest;
import frc.subsystem.test.DriveTest;
import frc.subsystem.test.GamepadTest;
import frc.subsystem.test.SubsystemTest;
import frc.utils.DriveSignal;
import frc.autonomous.Lvl2RightCloseRKT;

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
    private static final String kDefaultAuto = "Test";
    private static final String LvlTwoRightCloseRKT = "Lvl 2 Right to Close Rocket";
    private final SendableChooser<String> m_chooser = new SendableChooser<>();
    private final SendableChooser<String> testChooser = new SendableChooser<>();
    private final SubsystemManager subsystemManager = new SubsystemManager(Arrays.asList(
            Drive.getInstance(),
            Cargo.getInstance(),
            Elevator.getInstance(),
            Hatch.getInstance(),
            Jacks.getInstance()
    ));
    private final LinkedHashMap<String, Test> tests = new LinkedHashMap<>();
    private final Looper enabledLooper = new Looper();
    private final Looper disabledLooper = new Looper();
    private final GameController gameController = GameController.getInstance();
    private final Drive drive = Drive.getInstance();
    private final Cargo cargo = Cargo.getInstance();
    private final Elevator elevator = Elevator.getInstance();
    private final Hatch hatch = Hatch.getInstance();
    private final Jacks jacks = Jacks.getInstance();
    private final Vision vision = Vision.getInstance();
    private String m_autoSelected;
    private SubsystemTest subsystemTest;
    //public File trajectoryFile = Pathfinder.readFromCSV(EncodeTest.pf1.csv);
    private AutoBase autonomous = null;

    @Override
    public void robotInit() {
        m_chooser.setDefaultOption("Autoline", kDefaultAuto);
        m_chooser.addOption("Lvl 2 Right to Close Rocket", LvlTwoRightCloseRKT);
        Shuffleboard.getTab("Auton").add("Auto choices", m_chooser);
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
        drive.outputTelemetry();
    }

    @Override
    public void disabledInit() {
        enabledLooper.stop();
        disabledLooper.start();
        gameController.disabled();
    }

    @Override
    public void disabledPeriodic() {
        gameController.disabledPeriodic();
    }

    @Override
    public void autonomousInit() {
        disabledLooper.stop();
        enabledLooper.start();
        m_autoSelected = m_chooser.getSelected();
        System.out.println("Auto selected: " + m_autoSelected);
        switch (m_autoSelected) {
            case LvlTwoRightCloseRKT:
                autonomous = new Lvl2RightCloseRKT();
                break;
            case kDefaultAuto:
                autonomous = null;
                break;
            default:
                autonomous = null;
                break;
        }
    }

    @Override
    public void autonomousPeriodic() {
//        switch (m_autoSelected) {
//            case LvlTwoRightCloseRKT:
//                switch LvlTwoRightCloseRKTState {
//                    case PATH_FOLLOWING:
//                    File myFile = new File("LEVEL2_to_Rocket.pf1.csv");
//                    hatch.setHatchPlace();
//                    drive.setTrajectory(Pathfinder.readFromCSV(myFile));
//                    drive.updatePathFollower();
//                    elevator.pidToPosition(ElevatorPosition.HATCH_LOW);
//                    hatch.setHatchPull();
//                    myFile = new File("Right_RKT_Close_Backup.pf1.csv");
//                    drive.setTrajectory(Pathfinder.readFromCSV(myFile));
//                    drive.updatePathFollower();
//                    Insert gyro-turn 180 here
//                    myFile = new File("Close_Right_Rkt_to_FEED.pf1.csv");
//                    drive.setTrajectory(Pathfinder.readFromCSV(myFile));
//                    drive.updatePathFollower();
//                    hatch.setOpenLoop(100);
//                    }
//            case kDefaultAuto:
//            default:
//            myFile = new File("EncodeTest.pf1.csv");
//            drive.setTrajectory(Pathfinder.readFromCSV(myFile));
        if (autonomous != null) {
            autonomous.periodic(Timer.getFPGATimestamp());
        }
    }

    @Override
    public void teleopInit() {
        disabledLooper.stop();
        enabledLooper.start();
    }

    @Override
    public void teleopPeriodic() {
        final double t0 = Timer.getFPGATimestamp();
        drive.setOpenLoop(gameController.getDriveSignal());
        final double tDrive = Timer.getFPGATimestamp();

        if (gameController.placeHatch()) {
            hatch.setHatchPlace();
        } else if (gameController.pullHatch()) {
            hatch.setHatchPull();
        } else {
            hatch.setOpenLoop(gameController.hatchManual());
        }
        final double tHatch = Timer.getFPGATimestamp();

        cargo.intakeTilt(gameController.intakeTilt());
        if (gameController.cargoIntake()) {
            cargo.setDesiredState(IntakeState.INTAKE);
        } else if (gameController.cargoIntakeLeft()) {
            cargo.setDesiredState(IntakeState.INTAKE_LEFT);
        } else if (gameController.cargoIntakeRight()) {
            cargo.setDesiredState(IntakeState.INTAKE_RIGHT);
        } else if (gameController.cargoOuttakeLeft()) {
            cargo.setDesiredState(IntakeState.OUTTAKE_LEFT);
        } else if (gameController.cargoOuttakeRight()) {
            cargo.setDesiredState(IntakeState.OUTTAKE_RIGHT);
        } else if (gameController.cargoOuttakeFront()) {
            cargo.setDesiredState(IntakeState.OUTTAKE_FRONT);
        } else {
            cargo.setDesiredState(IntakeState.STOPPED);
        }
        final double tCargo = Timer.getFPGATimestamp();

        if (!gameController.manualJackWheelOverride()) {
            jacks.setWheels(gameController.runJackWheels());
        } else {
            jacks.setWheels(DriveSignal.NEUTRAL);
        }

        if (gameController.liftAllJacks()) {
            jacks.lift();
        } else if (gameController.retractAllJacks()) {
            jacks.retract();
        } else if (gameController.initializeHabClimbing()) {
            jacks.beginHabClimbLevel3();
        } else if (gameController.zeroJacks()) {
            jacks.beginZeroing();
        }
        final double tJacks = Timer.getFPGATimestamp();

        gameController.update();
        final double tController = Timer.getFPGATimestamp();

        if (gameController.setElevatorPositionLowHatch()) {
            elevator.pidToPosition(Elevator.ElevatorPosition.HATCH_LOW);
        } else if (gameController.setElevatorPositionMidHatch()) {
            elevator.pidToPosition(Elevator.ElevatorPosition.HATCH_MID);
        } else if (gameController.setElevatorPositionHighHatch()) {
            elevator.pidToPosition(Elevator.ElevatorPosition.HATCH_HIGH);
        } else if (gameController.setElevatorPositionLowCargo()) {
            elevator.pidToPosition(Elevator.ElevatorPosition.CARGO_LOW);
        } else if (gameController.setElevatorPositionMidCargo()) {
            elevator.pidToPosition(Elevator.ElevatorPosition.CARGO_MID);
        } else if (gameController.setElevatorPositionHighCargo()) {
            elevator.pidToPosition(Elevator.ElevatorPosition.CARGO_HIGH);
        } else {
            elevator.manualMovement(gameController.elevateManual());
        }
        final double tElevator = Timer.getFPGATimestamp();

        elevator.outputTelemetry();
//        hatch.outputTelemetry();
        jacks.outputTelemetry();
        drive.outputTelemetry();
        final double tOutput = Timer.getFPGATimestamp();

        ROBOT_MAIN_SHUFFLEBOARD.putNumber("TeleopPeriodicTimes/Drive", tDrive - t0);
        ROBOT_MAIN_SHUFFLEBOARD.putNumber("TeleopPeriodicTimes/Hatch", tHatch - tDrive);
        ROBOT_MAIN_SHUFFLEBOARD.putNumber("TeleopPeriodicTimes/Cargo", tCargo - tHatch);
        ROBOT_MAIN_SHUFFLEBOARD.putNumber("TeleopPeriodicTimes/Jacks", tJacks - tCargo);
        ROBOT_MAIN_SHUFFLEBOARD.putNumber("TeleopPeriodicTimes/Controller", tController - tJacks);
        ROBOT_MAIN_SHUFFLEBOARD.putNumber("TeleopPeriodicTimes/Elevator", tElevator - tController);
        ROBOT_MAIN_SHUFFLEBOARD.putNumber("TeleopPeriodicTimes/Output", tOutput - tElevator);
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
            case HATCH_MECHANISM_TEST:
                subsystemTest = null;
                break;
            case ELEVATOR_TEST:
                subsystemTest = null;
                break;
            case JACKS_TEST:
                subsystemTest = null;
                break;
        }
    }

    @Override
    public void testPeriodic() {
        if (subsystemTest != null) {
            subsystemTest.periodic(Timer.getFPGATimestamp());
        }
    }

    /**
     * Test cases that can be executed in the test mode to confirm functionality of various subsystems.
     */
    public enum Test {
        /**
         * Default, does not run any test.
         */
        DEFAULT_TEST("None"),
        /**
         * Chooses the {@link GamepadTest} as the test to run to confirm full functionality of the
         * {@link GameController}.
         */
        GAMEPAD_TEST("Gamepad Automated Test"),
        /**
         * Chooses the {@link DriveTest} as the test to run to confirm full functionality of the {@link Drive} subsystem
         */
        DRIVE_TEST("Drive Automated Test"),
        /**
         * Chooses the {@link CargoTest} as the test to run to confirm full functionality of the {@link Cargo} subsystem.
         */
        CARGO_TEST("Cargo Automated Test"),
        /**
         * TODO, will run an automated test on the {@link Hatch}.
         */
        HATCH_MECHANISM_TEST("Hatch Automated Test"),
        /**
         * TODO, will run an automated test on the {@link Elevator}.
         */
        ELEVATOR_TEST("Elevator Automated Test"),

        /**
         * TODO, will run an automated test on the {@link Jacks}.
         */
        JACKS_TEST("Test");

        private final String option;

        /**
         * Constructor.
         *
         * @param option the option to put on the dashboard chooser's list.
         */
        Test(String option) {
            this.option = option;
        }

        /**
         * Returns the option to put on the dashboard chooser's list.
         *
         * @return the option to put on the dashboard chooser's list.
         */
        String getOption() {
            return option;
        }
    }

}