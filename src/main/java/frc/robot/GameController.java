package frc.robot;

import frc.inputs.*;
import frc.utils.Constants;
import frc.utils.DriveSignal;

import static frc.utils.Constants.ROBOT_MAIN_SHUFFLEBOARD;

public class GameController implements GameHid {
    private static GameController instance;
    private final DriverHid driverHid;
    private final OperatorHid operatorHid;

    private GameController() {
        if (Constants.USE_JOYSTICK_FOR_DRIVING) {
            driverHid = DriverJoystick.getInstance();
        } else {
            driverHid = DriverGamepad.getInstance();
        }
        operatorHid = OperatorGamepad.getInstance();
    }

    public static GameController getInstance() {
        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }


    //
    // Driver controls
    //

    @Override
    public DriveSignal getDriveSignal() {
        // TODO(Raina)
        return driverHid.getDriveSignal();
    }

    @Override
    public boolean liftJack() {
        // TODO(Raina)
        return driverHid.liftJack();
    }


    //
    // Operator Controls
    //

    @Override
    public boolean cargoIntake() {
        return operatorHid.cargoIntake();
    }

    @Override
    public boolean cargoOuttakeFront() {
        return operatorHid.cargoOuttakeFront();
    }

    @Override
    public boolean cargoOuttakeRight() {
        return operatorHid.cargoOuttakeRight();
    }

    @Override
    public boolean cargoOuttakeLeft() {
        return operatorHid.cargoOuttakeLeft();
    }

    @Override
    public boolean setElevatorPositionLowCargo() {
        // TODO(Raina)
        return operatorHid.setElevatorPositionLowCargo();
    }

    @Override
    public boolean setElevatorPositionMidCargo() {
        // TODO(Raina)
        return operatorHid.setElevatorPositionMidCargo();
    }

    @Override
    public boolean setElevatorPositionHighCargo() {
        // TODO(Raina)
        return operatorHid.setElevatorPositionHighCargo();
    }

    @Override
    public boolean setElevatorPositionGroundHatch() {
        // TODO(Raina)
        return operatorHid.setElevatorPositionGroundHatch();
    }

    @Override
    public boolean setElevatorPositionLowHatch() {
        // TODO(Raina)
        return operatorHid.setElevatorPositionLowHatch();
    }

    @Override
    public boolean setElevatorPositionMidHatch() {
        // TODO(Raina)
        return operatorHid.setElevatorPositionMidHatch();
    }

    @Override
    public boolean setElevatorPositionHighHatch() {
        // TODO(Raina)
        return operatorHid.setElevatorPositionHighHatch();
    }

  @Override
  public double hatchManual() {
    return 0;
  }

    @Override
    public boolean hatchRelease() {
        // TODO(Raina)
        return operatorHid.hatchRelease();
    }

    public void outputTelemetry() {
        // TODO(Raina) add output for all variables
        ROBOT_MAIN_SHUFFLEBOARD.putBoolean("Cargo Intake", this.cargoIntake());
        ROBOT_MAIN_SHUFFLEBOARD.putBoolean("Cargo Outtake Front", this.cargoOuttakeFront());
        ROBOT_MAIN_SHUFFLEBOARD.putBoolean("Cargo Outtake Left", this.cargoOuttakeLeft());
        ROBOT_MAIN_SHUFFLEBOARD.putBoolean("Cargo Outtake Right", this.cargoOuttakeRight());
        ROBOT_MAIN_SHUFFLEBOARD.putNumber("Drive Signal Left", this.getDriveSignal().getLeftOutput());
        ROBOT_MAIN_SHUFFLEBOARD.putNumber("Drive Signal Right", this.getDriveSignal().getRightOutput());
    }
}