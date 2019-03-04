package frc.inputs;

import edu.wpi.first.wpilibj.DriverStation;
import frc.states.IntakeState;
import frc.utils.DriveSignal;

import static frc.utils.Constants.DRIVER_HID_OPTION;

/**
 * An implementation of the Driver's controls for when the robot is being
 * operated with a gamepad.
 */
public final class GameController implements GameHid {
    private static GameController instance;
    private final DriverHid driverHid;
    private final OperatorHid operatorHid;

    /**
     * Constructor.
     */
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

    /**
     * Returns a static instance of the {@link GameController} class. If none has been created yet, the instance
     * is created. This enables multiple any other classes to use this class without having to pass an instance or take
     * the risk of trying to instantiate multiple instances of this class, which would result in errors.
     *
     * @return a static instance of the {@link GameController} subsystem.
     */
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
    public boolean initializeHabClimbingLevel3() {
        return driverHid.initializeHabClimbingLevel3();
    }

    @Override
    public boolean initializeHabClimbingLevel2() {
        return driverHid.initializeHabClimbingLevel2();
    }

    @Override
    public boolean manualJackOverride() {
        return driverHid.manualJackOverride();
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
    public boolean placeHatch() {
        return operatorHid.placeHatch();
    }

    @Override
    public boolean pullHatch() {
        return operatorHid.pullHatch();
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

    public IntakeState getDesiredCargoIntakeState() {
        if (this.cargoIntake()) {
            return IntakeState.INTAKE;
        } else if (this.cargoIntakeLeft()) {
            return IntakeState.INTAKE_LEFT;
        } else if (this.cargoIntakeRight()) {
            return IntakeState.INTAKE_RIGHT;
        } else if (this.cargoOuttakeLeft()) {
            return IntakeState.OUTTAKE_LEFT;
        } else if (this.cargoOuttakeRight()) {
            return IntakeState.OUTTAKE_RIGHT;
        } else if (this.cargoOuttakeFront()) {
            return IntakeState.OUTTAKE_FRONT;
        } else {
            return IntakeState.STOPPED;
        }
    }
}