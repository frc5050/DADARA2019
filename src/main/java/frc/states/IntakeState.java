package frc.states;

public enum IntakeState {
    INTAKE(true, 0.0, 1.0, 1.0, 1.0),
    INTAKE_LEFT(true, -1.0, 0.0, -1.0, 0.0),
    INTAKE_RIGHT(true, 1.0, -1.0, 0.0, 0.0),
    OUTTAKE_FRONT(false, 0.0, -1.0, 1.0, -1.0),
    OUTTAKE_LEFT(false, 1.0, 1.0, 1.0, 0.0),
    OUTTAKE_RIGHT(false, -1.0, 1.0, 1.0, 0.0),
    STOPPED(false, 0.0, 0.0, 0.0, 0.0);

    private final boolean stopOnSensor;
    private final double rearMotorOutput;
    private final double leftMotorOutput;
    private final double rightMotorOutput;
    private final double intakeOutput;

    IntakeState(final boolean stopOnSensor, final double rearMotorOutput, final double leftMotorOutput, final double rightMotorOutput, final double intakeOutput) {
        this.stopOnSensor = stopOnSensor;
        this.rearMotorOutput = rearMotorOutput;
        this.leftMotorOutput = leftMotorOutput;
        this.rightMotorOutput = rightMotorOutput;
        this.intakeOutput = intakeOutput;
    }

    public boolean getStopOnSensor() {
        return stopOnSensor;
    }

    public double getRearMotorOutput() {
        return rearMotorOutput;
    }

    public double getLeftMotorOutput() {
        return leftMotorOutput;
    }

    public double getRightMotorOutput() {
        return rightMotorOutput;
    }

    public double getIntakeOutput() {
        return intakeOutput;
    }
}
