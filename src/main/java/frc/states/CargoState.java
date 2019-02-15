package frc.states;

public class CargoState {
    // Inputs
    public IntakeState intakeState = IntakeState.STOPPED;
    public boolean ballInHold = false;

    // Outputs
    public double rearMotorOutput;
    public double rightMotorOutput;
    public double leftMotorOutput;
    public double intakeOutput;
    public double intakeTiltOutput;
}
