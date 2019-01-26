package frc.robot;

import frc.inputs.*;
import frc.utils.Constants;
import frc.utils.DriveSignal;

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
        return operatorHid.setElevatorPositionLowCargo();
    }

    @Override
    public boolean setElevatorPositionMidCargo() {
        return operatorHid.setElevatorPositionMidCargo();
    }

    @Override
    public boolean setElevatorPositionHighCargo() {
        return operatorHid.setElevatorPositionHighCargo();
    }

    @Override
    public boolean setElevatorPositionGroundHatch() {
        return operatorHid.setElevatorPositionGroundHatch();
    }

    @Override
    public boolean setElevatorPositionLowHatch() {
        return operatorHid.setElevatorPositionLowHatch();
    }

    @Override
    public boolean setElevatorPositionMidHatch() {
        return operatorHid.setElevatorPositionMidHatch();
    }

    @Override
    public boolean setElevatorPositionHighHatch() {
        return operatorHid.setElevatorPositionHighHatch();
    }

    @Override
    public double hatchManual() {
        return operatorHid.hatchManual();
    }

    @Override
    public double elevateManual() {
        return operatorHid.elevateManual();
    }

    @Override
    public boolean hatchRelease() {
        return operatorHid.hatchRelease();
    }
}