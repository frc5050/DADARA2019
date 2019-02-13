package frc.inputs;

import edu.wpi.first.wpilibj.DriverStation;
import frc.utils.DriveSignal;

import static frc.utils.Constants.DRIVER_HID_OPTION;

public class GameController implements GameHid {
    private static GameController instance;
    private final DriverHid driverHid;
    private final OperatorHid operatorHid;

    private GameController() {
        switch (DRIVER_HID_OPTION) {
            case SINGLE_JOYSTICK:
                driverHid = DriverJoystick.getInstance();
                break;
            case DUAL_JOYSTICKS:
                driverHid = DriverDoubleJoysticks.getInstance();
                break;
            case GAMEPAD:
                driverHid = DriverGamepad.getInstance();
                break;
            default:
                driverHid = DriverGamepad.getInstance();
                DriverStation.reportError("GameController driver hid reached default case", false);
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
    public boolean liftAllJacks() {
        return driverHid.liftAllJacks();
    }

    @Override
    public boolean retractAllJacks() {
        return driverHid.retractAllJacks();
    }

    @Override
    public boolean initializeHabClimbing() {
        return driverHid.initializeHabClimbing();
    }

    @Override
    public boolean manualJackWheelOverride() {
        return driverHid.manualJackWheelOverride();
    }

    @Override
    public boolean zeroJacks() {
        return driverHid.zeroJacks();
    }

    @Override
    public DriveSignal runJackWheels() {
        return driverHid.runJackWheels();
    }

    @Override
    public boolean cargoOuttakeRight() {
        return driverHid.cargoOuttakeRight();
    }

    @Override
    public boolean cargoOuttakeLeft() {
        return driverHid.cargoOuttakeLeft();
    }

    @Override
    public boolean cargoIntakeRight() {
        return driverHid.cargoIntakeRight();
    }

    @Override
    public boolean cargoIntakeLeft() {
        return driverHid.cargoIntakeLeft();
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
    public boolean hatchFeederHeight() {
        return operatorHid.hatchFeederHeight();
    }


    //
    // Controls for both
    //

    @Override
    public void update() {
        operatorHid.update();
        driverHid.update();
    }

    @Override
    public void disabled() {
        operatorHid.disabled();
        driverHid.disabled();
    }

    @Override
    public void disabledPeriodic() {
        operatorHid.disabledPeriodic();
        driverHid.disabledPeriodic();
    }
}