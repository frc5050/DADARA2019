package frc.inputs;

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
        return driverHid.getDriveSignal();
    }

    @Override
    public boolean liftJack() {
        return driverHid.liftJack();
    }

    @Override
    public boolean extendFrontJack() {
        return driverHid.extendFrontJack();
    }

    @Override
    public boolean retractFrontJack() {
        return driverHid.retractFrontJack();
    }

    @Override
    public boolean extendLeftJack() {
        return driverHid.extendLeftJack();
    }

    @Override
    public boolean retractLeftJack() {
        return driverHid.retractLeftJack();
    }

    @Override
    public boolean extendRightJack() {
        return driverHid.extendRightJack();
    }

    @Override
    public boolean retractRightJack() {
        return driverHid.retractRightJack();
    }

    @Override
    public DriveSignal runJackWheels() {
        return driverHid.runJackWheels();
    }

    @Override
    public boolean holdAll() {
        return driverHid.holdAll();
    }


    //
    // Operator Controls
    //

    @Override
    public void update() {
        operatorHid.update();
    }

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
    public boolean cargoIntakeRight() {
        return operatorHid.cargoIntakeRight();
    }

    @Override
    public boolean cargoIntakeLeft() {
        return operatorHid.cargoIntakeLeft();
    }

  @Override
  public double intakeTilt() {
    return operatorHid.intakeTilt();
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
    public boolean useHatchOpenLoop() {
        return operatorHid.useHatchOpenLoop();
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