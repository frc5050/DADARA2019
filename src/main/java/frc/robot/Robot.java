/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import frc.inputs.GameController;
import frc.loops.Looper;
import frc.subsystem.*;
import frc.utils.DriveSignal;

import java.util.Arrays;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
    //    private LinkedHashMap<String, Test> tests = new LinkedHashMap<>();
    //    private final SendableChooser<String> testChooser = new SendableChooser<>();

    private final SubsystemManager subsystemManager = new SubsystemManager(Arrays.asList(
            Drive.getInstance(),
            Cargo.getInstance(),
            Velocivator.getInstance(),
//            Hatch2.getInstance(),
            Jacks.getInstance()
    ));

    private Looper enabledLooper = new Looper();
    private Looper disabledLooper = new Looper();

    private GameController gameController = GameController.getInstance();

    private Drive drive = Drive.getInstance();
//    private Cargo cargo = Cargo.getInstance();
    private Velocivator elevator = Velocivator.getInstance();
//    private Hatch2 hatch = Hatch2.getInstance();
//    private Jacks jacks = Jacks.getInstance();
    private Jacks jacks = Jacks.getInstance();
//    private Vision vision = Vision.getInstance();
//    private SubsystemTest subsystemTest;

    @Override
    public void robotInit() {
//        tests.put(Test.DEFAULT_TEST.getOption(), Test.DEFAULT_TEST);
//        testChooser.setDefaultOption(Test.DEFAULT_TEST.getOption(), Test.DEFAULT_TEST.getOption());
//
//        for (Test test : Test.values()) {
//            if (test != Test.DEFAULT_TEST) {
//                tests.put(test.getOption(), test);
//            }
//        }
//
//        for (String testKey : tests.keySet()) {
//            testChooser.addOption(testKey, testKey);
//        }

        subsystemManager.registerEnabledLoops(enabledLooper);
        subsystemManager.registerDisabledLoop(disabledLooper);
    }

    @Override
    public void robotPeriodic() {

    }

    @Override
    public void disabledInit() {
        enabledLooper.stop();
        disabledLooper.start();
    }

    @Override
    public void disabledPeriodic() {

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
//        hatch.setOpenLoop(gameController.hatchManual());
//        cargo.intakeTilt(gameController.intakeTilt());
//        if (gameController.cargoIntake()) {
//            cargo.setDesiredState(CargoState.IntakeState.INTAKE);
//        } else if (gameController.cargoIntakeLeft()) {
//            cargo.setDesiredState(CargoState.IntakeState.INTAKE_LEFT);
//        } else if (gameController.cargoIntakeRight()) {
//            cargo.setDesiredState(CargoState.IntakeState.INTAKE_RIGHT);
//        } else if (gameController.cargoOuttakeLeft()) {
//            cargo.setDesiredState(CargoState.IntakeState.OUTTAKE_LEFT);
//        } else if (gameController.cargoOuttakeRight()) {
//            cargo.setDesiredState(CargoState.IntakeState.OUTTAKE_RIGHT);
//        } else if (gameController.cargoOuttakeFront()) {
//            cargo.setDesiredState(CargoState.IntakeState.OUTTAKE_FRONT);
//        } else {
//            cargo.setDesiredState(CargoState.IntakeState.STOPPED);
//        }


        if(!gameController.manualJackWheelOverride()) {
            jacks.setWheels(gameController.runJackWheels());
        } else {
            jacks.setWheels(DriveSignal.NEUTRAL);
        }

        if(gameController.liftAllJacks()){
            System.out.println("Should be lifting");
            jacks.lift();
        } else if(gameController.retractAllJacks()) {
            System.out.println("Should be retracting");
            jacks.retract();
        } else if(gameController.initializeHabClimbing()) {
            System.out.println("Should init hab climb...");
            jacks.beginHabClimb();
        } else if(gameController.zeroJacks()){
            System.out.println("Should be zeroing");
            jacks.beginZeroing();
        }
//        else {
//            jacks.setOpenLoop(0.0);
//        }



        gameController.update();
        if (gameController.setElevatorPositionLowHatch()) {
            elevator.pidToPosition(Velocivator.ElevatorPosition.HATCH_LOW);
        } else if (gameController.setElevatorPositionMidHatch()) {
            elevator.pidToPosition(Velocivator.ElevatorPosition.HATCH_MID);
        } else if (gameController.setElevatorPositionHighHatch()) {
            elevator.pidToPosition(Velocivator.ElevatorPosition.HATCH_HIGH);
        } else if (gameController.setElevatorPositionLowCargo()) {
            elevator.pidToPosition(Velocivator.ElevatorPosition.CARGO_LOW);
        } else if (gameController.setElevatorPositionMidCargo()) {
            elevator.pidToPosition(Velocivator.ElevatorPosition.CARGO_MID);
        } else if (gameController.setElevatorPositionHighCargo()) {
            elevator.pidToPosition(Velocivator.ElevatorPosition.CARGO_HIGH);
        } else {
            elevator.manualMovement(gameController.elevateManual());
        }

        elevator.outputTelemetry();
//        hatch.outputTelemetry();
        jacks.outputTelemetry();
    }

    // TODO add more tests
    // TODO automate test validation
    @Override
    public void testInit() {
//        Test testSelected = tests.get(testChooser.getSelected());
//        disabledLooper.stop();
//        enabledLooper.stop();
//        switch (testSelected) {
//            case DEFAULT_TEST:
//                subsystemTest = null;
//                break;
//            case GAMEPAD_TEST:
//                subsystemTest = new GamepadTest();
//                break;
//            case DRIVE_TEST:
//                subsystemTest = new DriveTest();
//                break;
//            case CARGO_TEST:
//                subsystemTest = new CargoTest();
//                break;
//            case HATCH_MECHANISM_TEST:
//                break;
//            case ELEVATOR_TEST:
//                subsystemTest = null;
//                break;
//            case JACKS_TEST:
//                subsystemTest = null;
//                break;
//            case ROBOT_STATE_TEST:
//                subsystemTest = null;
//                break;
//        }
    }

    @Override
    public void testPeriodic() {
//        subsystemTest.periodic(Timer.getFPGATimestamp());
    }

    public void outputTelemetry() {
//        subsystemManager.outputTelemetry();
//        enabledLooper.outputTelemetry();
    }
//
//    public enum Test {
//        DEFAULT_TEST("None"),
//        GAMEPAD_TEST("Gamepad Automated Test"),
//        DRIVE_TEST("Drive Automated Test"),
//        CARGO_TEST("Cargo Automated Test"),
//        HATCH_MECHANISM_TEST("Hatch Automated Test"),
//        ELEVATOR_TEST("Elevator Automated Test"),
//        JACKS_TEST("Test"),
//        ROBOT_STATE_TEST("Robot State Test");
//
//
//        private String option;
//
//        Test(String option) {
//            this.option = option;
//        }
//
//        public String getOption() {
//            return option;
//        }
//    }

}
