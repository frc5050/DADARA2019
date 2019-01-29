package frc.states;

public class CargoStateMachine {
    // These should probably always be positive, we can negate motors individually
    private static final double DEFAULT_OUTTAKE_POWER = 1.0;
    private static final double DEFAULT_INTAKE_POWER = 1.0;

    private CargoState systemState = new CargoState();
    private CargoState.IntakeState desiredIntakeState = CargoState.IntakeState.STOPPED;

    public synchronized void setDesiredState(CargoState.IntakeState intakeState) {
        desiredIntakeState = intakeState;
    }

    public synchronized CargoState onUpdate(CargoState currentState) {
        // If ball is in hold, don't allow running the intake any more
        if (currentState.ballInHold && desiredIntakeState.getStopOnSensor()) {
            systemState.intakeState = CargoState.IntakeState.STOPPED;
        } else {
            systemState.intakeState = desiredIntakeState;
        }

        handleSystemStateUpdate();

        // Update whether the ball is in the hold after checking whether the systemState is equal to the current state
        // since it has no bearing on the output (we don't want to needlessly update things)
        systemState.ballInHold = currentState.ballInHold;

        return systemState;
    }

    private synchronized void handleSystemStateUpdate() {
        switch (systemState.intakeState) {
            case INTAKE:
                systemState.rearMotorOutput = 0.0;
                systemState.leftMotorOutput = DEFAULT_INTAKE_POWER;
                systemState.rightMotorOutput = DEFAULT_INTAKE_POWER;
                systemState.intakeOutput = DEFAULT_INTAKE_POWER;
                break;
            case INTAKE_LEFT:
                systemState.rearMotorOutput = -DEFAULT_OUTTAKE_POWER;
                systemState.leftMotorOutput = 0.0;
                systemState.rightMotorOutput = -DEFAULT_OUTTAKE_POWER;
                systemState.intakeOutput = 0.0;
                break;
            case INTAKE_RIGHT:
                systemState.rearMotorOutput = DEFAULT_OUTTAKE_POWER;
                systemState.leftMotorOutput = -DEFAULT_OUTTAKE_POWER;
                systemState.rightMotorOutput = 0.0;
                systemState.intakeOutput = 0.0;
                break;
            case OUTTAKE_FRONT:
                systemState.rearMotorOutput = 0.0;
                systemState.leftMotorOutput = -DEFAULT_OUTTAKE_POWER;
                systemState.rightMotorOutput = -DEFAULT_OUTTAKE_POWER;
                systemState.intakeOutput = -DEFAULT_OUTTAKE_POWER;
                break;
            case OUTTAKE_LEFT:
                systemState.rearMotorOutput = DEFAULT_OUTTAKE_POWER;
                systemState.leftMotorOutput = DEFAULT_OUTTAKE_POWER;
                systemState.rightMotorOutput = DEFAULT_OUTTAKE_POWER;
                systemState.intakeOutput = 0.0;
                break;
            case OUTTAKE_RIGHT:
                systemState.rearMotorOutput = -DEFAULT_OUTTAKE_POWER;
                systemState.leftMotorOutput = DEFAULT_OUTTAKE_POWER;
                systemState.rightMotorOutput = DEFAULT_OUTTAKE_POWER;
                systemState.intakeOutput = 0.0;
                break;
            case STOPPED:
                systemState.rearMotorOutput = 0.0;
                systemState.leftMotorOutput = 0.0;
                systemState.rightMotorOutput = 0.0;
                systemState.intakeOutput = 0.0;
                break;
        }
    }
}
