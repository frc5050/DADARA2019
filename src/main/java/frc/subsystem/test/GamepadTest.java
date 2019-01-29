package frc.subsystem.test;

import frc.inputs.GameController;

import static frc.utils.Constants.ROBOT_MAIN_SHUFFLEBOARD;

public class GamepadTest implements SubsystemTest {
    private GameController gameController = GameController.getInstance();

    @Override
    public void periodic(double timestamp) {
        // TODO, do nothing?
    }

    @Override
    public void outputTelemetry() {
        ROBOT_MAIN_SHUFFLEBOARD.putNumber("Drive Signal Left", gameController.getDriveSignal().getLeftOutput());
        ROBOT_MAIN_SHUFFLEBOARD.putNumber("Drive Signal Right", gameController.getDriveSignal().getRightOutput());
        ROBOT_MAIN_SHUFFLEBOARD.putBoolean("Cargo Intake", gameController.cargoIntake());
        ROBOT_MAIN_SHUFFLEBOARD.putBoolean("Cargo Outtake Front", gameController.cargoOuttakeFront());
        ROBOT_MAIN_SHUFFLEBOARD.putBoolean("Cargo Outtake Left", gameController.cargoOuttakeLeft());
        ROBOT_MAIN_SHUFFLEBOARD.putBoolean("Cargo Outtake Right", gameController.cargoOuttakeRight());
        ROBOT_MAIN_SHUFFLEBOARD.putBoolean("Lift Jack", gameController.liftJack());
        ROBOT_MAIN_SHUFFLEBOARD.putBoolean("Set Elevator Position Low Cargo", gameController.setElevatorPositionLowCargo());
        ROBOT_MAIN_SHUFFLEBOARD.putBoolean("Set Elevator Position Mid Cargo", gameController.setElevatorPositionMidCargo());
        ROBOT_MAIN_SHUFFLEBOARD.putBoolean("Set Elevator Position High Cargo", gameController.setElevatorPositionHighCargo());
        ROBOT_MAIN_SHUFFLEBOARD.putBoolean("Set Elevator Position Low Hatch", gameController.setElevatorPositionLowHatch());
        ROBOT_MAIN_SHUFFLEBOARD.putBoolean("Set Elevator Position Mid Hatch", gameController.setElevatorPositionMidHatch());
        ROBOT_MAIN_SHUFFLEBOARD.putBoolean("Set Elevator Position High Hatch", gameController.setElevatorPositionHighHatch());
        ROBOT_MAIN_SHUFFLEBOARD.putNumber("Hatch Manual", gameController.hatchManual());
        ROBOT_MAIN_SHUFFLEBOARD.putBoolean("Hatch Release", gameController.hatchRelease());
    }
}
